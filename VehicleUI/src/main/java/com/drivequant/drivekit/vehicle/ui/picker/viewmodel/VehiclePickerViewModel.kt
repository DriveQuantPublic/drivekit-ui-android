package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.vehicle.DriveKitVehiclePicker
import com.drivequant.drivekit.vehicle.enum.VehicleBrand
import com.drivequant.drivekit.vehicle.enum.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.picker.*
import com.drivequant.drivekit.vehicle.picker.VehiclePickerStatus.*
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import java.io.Serializable

class VehiclePickerViewModel: ViewModel(), Serializable {
    val stepDispatcher : MutableLiveData<VehiclePickerStep> = MutableLiveData()

    var itemTypes: List<VehiclePickerItem> = listOf()
    var itemCategories: List<VehiclePickerItem> = listOf()
    var itemBrands: List<VehiclePickerItem> = listOf()
    var itemEngines: List<VehiclePickerItem> = listOf()
    var itemModels: List<VehiclePickerItem> = listOf()
    var itemYears: List<VehiclePickerItem> = listOf()
    var itemVersions: List<VehiclePickerItem> = listOf()

    var isLiteConfig = false

    lateinit var selectedVehicleType: VehicleType
    lateinit var selectedCategory: VehicleCategoryItem
    lateinit var selectedBrand: VehicleBrand
    lateinit var selectedEngineIndex: VehicleEngineIndex
    lateinit var selectedModel: String
    lateinit var selectedYear: String
    lateinit var selectedVersion: VehicleVersion
    lateinit var characteristics: VehicleCharacteristics

    fun computeNextScreen(context: Context, currentStep: VehiclePickerStep?, viewConfig: VehiclePickerViewConfig, otherAction: Boolean = false){
        when (currentStep){
            null -> {
                itemTypes = fetchVehicleTypes(context, viewConfig)
                if (itemTypes.size == 1){
                    selectedVehicleType = VehicleType.valueOf(itemTypes.first().value)
                    computeNextScreen(context, TYPE, viewConfig)
                } else {
                    stepDispatcher.postValue(TYPE)
                }
            }
            TYPE -> {
                if (viewConfig.categoryTypes != CategoryType.BRANDS_CONFIG_ONLY) {
                    itemCategories = fetchVehicleCategories(context)
                    stepDispatcher.postValue(CATEGORY)
                } else {
                    if (viewConfig.displayBrandsWithIcons){
                        itemBrands = fetchVehicleBrands(context, withIcons = true)
                        stepDispatcher.postValue(BRANDS_ICONS)
                    } else {
                        itemBrands = fetchVehicleBrands(context)
                        stepDispatcher.postValue(BRANDS_FULL)
                    }
                }
            }
            CATEGORY -> {
                stepDispatcher.postValue(CATEGORY_DESCRIPTION)
            }
            CATEGORY_DESCRIPTION -> {
                if (!otherAction) {
                    isLiteConfig = true
                    selectedVersion = VehicleVersion("", selectedCategory.liteConfigDqIndex)
                    fetchVehicleCharacteristics()
                } else {
                    isLiteConfig = false
                    if (viewConfig.displayBrandsWithIcons){
                        itemBrands = fetchVehicleBrands(context, withIcons = true)
                        stepDispatcher.postValue(BRANDS_ICONS)
                    } else {
                        itemBrands = fetchVehicleBrands(context)
                        stepDispatcher.postValue(BRANDS_FULL)
                    }
                }
            }
            BRANDS_ICONS,
            BRANDS_FULL -> {
                itemEngines = fetchVehicleEngines(context)
                stepDispatcher.postValue(ENGINE)
            }
            ENGINE -> fetchVehicleModels()
            MODELS -> fetchVehicleYears()
            YEARS -> fetchVehicleVersions()
            VERSIONS -> fetchVehicleCharacteristics()
            NAME -> TODO()
        }
    }

    fun getItemsByStep(vehiclePickerStep: VehiclePickerStep): List<VehiclePickerItem> {
        return when (vehiclePickerStep){
            TYPE -> itemTypes
            CATEGORY -> itemCategories
            BRANDS_ICONS -> itemBrands
            BRANDS_FULL -> itemBrands
            ENGINE -> itemEngines
            MODELS -> itemModels
            YEARS -> itemYears
            VERSIONS -> itemVersions
            else -> listOf()
        }
    }

    private fun fetchVehicleTypes(context: Context, viewConfig: VehiclePickerViewConfig): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        for (i in viewConfig.vehicleTypes.indices){
            val currentType = viewConfig.vehicleTypes[i]
            items.add(i, VehiclePickerItem(i, currentType.getTitle(context), currentType.name))
        }
        return items
    }

    private fun fetchVehicleCategories(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawCategories = selectedVehicleType.getCategories(context)
        for (i in rawCategories.indices){
            items.add(i, VehiclePickerItem(i, rawCategories[i].title, rawCategories[i].category, rawCategories[i].icon1, rawCategories[i].icon2))
        }
        return items
    }

    private fun fetchVehicleBrands(context: Context, withIcons: Boolean = false): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = selectedVehicleType.getBrands(context, withIcons)
        for (i in rawBrands.indices){
            items.add(i, VehiclePickerItem(i, rawBrands[i].brand.value, rawBrands[i].brand.name, rawBrands[i].icon))
        }
        return items
    }

    private fun fetchVehicleEngines(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = selectedVehicleType.getEngines(context)
        for (i in rawBrands.indices){
            items.add(i, VehiclePickerItem(i, rawBrands[i].title, rawBrands[i].engine.toString()))
        }
        return items
    }

    private fun fetchVehicleModels() {
        DriveKitVehiclePicker.getModels(selectedBrand, selectedEngineIndex, object : VehicleModelsQueryListener{
            override fun onResponse(status: VehiclePickerStatus, models: List<String>) {
                when (status){
                    SUCCESS -> {
                        itemModels = buildItemsFromStrings(models)
                        stepDispatcher.postValue(MODELS)
                    }
                    //FAILED_TO_RETRIEVED_DATA ->
                    //NO_RESULT ->
                }
            }
        })
    }

    private fun fetchVehicleYears() {
        DriveKitVehiclePicker.getYears(selectedBrand, selectedEngineIndex, selectedModel, object : VehicleYearsQueryListener {
            override fun onResponse(status: VehiclePickerStatus, years: List<String>) {
                when (status){
                    SUCCESS -> {
                        itemYears = buildItemsFromStrings(years)
                        stepDispatcher.postValue(YEARS)
                    }
                    //FAILED_TO_RETRIEVED_DATA ->
                    //NO_RESULT ->
                }
            }
        })
    }

    private fun fetchVehicleVersions() {
        DriveKitVehiclePicker.getVersions(selectedBrand, selectedEngineIndex, selectedModel, selectedYear, object : VehicleVersionsQueryListener {
            override fun onResponse(status: VehiclePickerStatus, versions: List<VehicleVersion>) {
                when (status){
                    SUCCESS -> {
                        itemVersions = buildItemsFromVersions(versions)
                        stepDispatcher.postValue(VERSIONS)
                    }
                    //FAILED_TO_RETRIEVED_DATA ->
                    //_RESULT ->
                }
            }
        })
    }

    private fun fetchVehicleCharacteristics(){
        DriveKitVehiclePicker.getVehicle(selectedVersion, object : VehicleDqVehicleQueryListener {
            override fun onResponse(status: VehiclePickerStatus, vehicleCharacteristics: VehicleCharacteristics) {
                // TODO check VehiclePickerStatus
                characteristics = vehicleCharacteristics
                stepDispatcher.postValue(NAME)
            }
        })
    }

    fun validateCategory(context: Context, viewConfig: VehiclePickerViewConfig){
        computeNextScreen(context, CATEGORY_DESCRIPTION, viewConfig)
    }

    fun getDefaultVehicleName(): String? {
        return if (isLiteConfig){
            selectedCategory.title
        } else {
            "$selectedBrand $selectedModel ${selectedVersion.version}"
        }
    }

    private fun buildItemsFromStrings(source: List<String>) : MutableList<VehiclePickerItem> {
        val list: MutableList<VehiclePickerItem> = mutableListOf()
        for (i in source.indices){
            list.add(VehiclePickerItem(i, source[i], source[i]))
        }
        return list
    }

    private fun buildItemsFromVersions(source: List<VehicleVersion>) : MutableList<VehiclePickerItem> {
        val list: MutableList<VehiclePickerItem> = mutableListOf()
        for (i in source.indices){
            list.add(VehiclePickerItem(i, source[i].version, source[i].dqIndex))
        }
        return list
    }

    class VehiclePickerViewModelFactory : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return VehiclePickerViewModel() as T
        }
    }
}