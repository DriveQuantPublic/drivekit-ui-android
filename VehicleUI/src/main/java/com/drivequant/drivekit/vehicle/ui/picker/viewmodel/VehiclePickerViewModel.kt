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
    val itemDataList: MutableLiveData<Map<VehiclePickerStep, List<VehiclePickerItem>>> = MutableLiveData()
    val vehicleCharacteristicsData: MutableLiveData<VehicleCharacteristics> = MutableLiveData()

    var itemTypes: List<VehiclePickerItem> = listOf()
    var itemCategories: List<VehiclePickerItem> = listOf()
    var itemBrands: List<VehiclePickerItem> = listOf()
    var itemEngines: List<VehiclePickerItem> = listOf()
    var itemModels: List<VehiclePickerItem> = listOf()
    var itemYears: List<VehiclePickerItem> = listOf()
    var itemVersions: List<VehiclePickerItem> = listOf()

    lateinit var selectedVehicleType: VehicleType
    lateinit var selectedCategory: VehicleCategoryItem
    lateinit var selectedBrand: VehicleBrand
    lateinit var selectedEngine: VehicleEngineIndex
    lateinit var selectedModel: String
    lateinit var selectedYear: String
    lateinit var selectedVersion: VehicleVersion

    fun computeNextScreen(context: Context, currentStep: VehiclePickerStep?, viewConfig: VehiclePickerViewConfig){
        when (currentStep){
            TYPE -> {
                itemCategories = fetchVehicleCategories(context, selectedVehicleType)
                val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                map[CATEGORY] = itemCategories
                itemDataList.postValue(map)
            }
            CATEGORY -> {
                itemBrands = fetchVehicleBrands(context, selectedVehicleType, withIcons = false)
                val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                map[BRANDS_FULL] = itemBrands
                itemDataList.postValue(map)
            }
            CATEGORY_DESCRIPTION -> TODO()
            BRANDS_ICONS, BRANDS_FULL -> {
                itemEngines = fetchVehicleEngines(context, selectedVehicleType)
                val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                map[ENGINE] = itemEngines
                itemDataList.postValue(map)
            }
            ENGINE -> fetchVehicleModels(selectedBrand, selectedEngine)
            MODELS -> fetchVehicleYears(selectedVehicleType, selectedBrand, selectedEngine, selectedModel)
            YEARS -> fetchVehicleVersions(selectedVehicleType, selectedBrand, selectedEngine, selectedModel, selectedYear)
            VERSIONS -> fetchVehicleCharacteristics(selectedVersion)
            NAME -> TODO()
            null -> {
                itemTypes = fetchVehicleTypes(context, viewConfig)
                val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                map[TYPE] = itemTypes
                itemDataList.postValue(map)
            }
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
            CATEGORY_DESCRIPTION, NAME -> listOf()
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

    private fun fetchVehicleCategories(context: Context, vehicleType: VehicleType): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawCategories = vehicleType.getCategories(context)
        for (i in rawCategories.indices){
            items.add(i, VehiclePickerItem(i, rawCategories[i].title, rawCategories[i].category, rawCategories[i].icon1, rawCategories[i].icon2))
        }
        return items
    }

    private fun fetchVehicleBrands(context: Context, vehicleType: VehicleType, withIcons: Boolean = false): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = vehicleType.getBrands(context, withIcons)
        for (i in rawBrands.indices){
            items.add(i, VehiclePickerItem(i, rawBrands[i].brand.value, rawBrands[i].brand.name, rawBrands[i].icon))
        }
        return items
    }

    private fun fetchVehicleEngines(context: Context, vehicleType: VehicleType): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = vehicleType.getEngines(context)
        for (i in rawBrands.indices){
            items.add(i, VehiclePickerItem(i, rawBrands[i].title, rawBrands[i].engine.toString()))
        }
        return items
    }

    private fun fetchVehicleModels(vehicleBrand: VehicleBrand, vehicleEngineIndex: VehicleEngineIndex) {
        DriveKitVehiclePicker.getModels(vehicleBrand, vehicleEngineIndex, object : VehicleModelsQueryListener{
            override fun onResponse(status: VehiclePickerStatus, models: List<String>) {
                when (status){
                    SUCCESS -> {
                        itemModels = buildItemsFromStrings(models)
                        val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                        map[MODELS] = itemTypes
                        itemDataList.postValue(map)
                    }
                    //FAILED_TO_RETRIEVED_DATA ->
                    //NO_RESULT ->
                }
            }
        })
    }

    private fun fetchVehicleYears(vehicleType: VehicleType, vehicleBrand: VehicleBrand, vehicleEngineIndex: VehicleEngineIndex, vehicleModel: String) {
        DriveKitVehiclePicker.getYears(vehicleBrand, vehicleEngineIndex, vehicleModel, object : VehicleYearsQueryListener {
            override fun onResponse(status: VehiclePickerStatus, years: List<String>) {
                when (status){
                    SUCCESS -> {
                        itemYears = buildItemsFromStrings(years)
                        val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                        map[YEARS] = itemYears
                        itemDataList.postValue(map)
                    }
                    //FAILED_TO_RETRIEVED_DATA ->
                    //NO_RESULT ->
                }
            }
        })
    }

    private fun fetchVehicleVersions(vehicleType: VehicleType, vehicleBrand: VehicleBrand, vehicleEngineIndex: VehicleEngineIndex, vehicleModel: String, vehicleYear: String) {
        DriveKitVehiclePicker.getVersions(vehicleBrand, vehicleEngineIndex, vehicleModel, vehicleYear, object : VehicleVersionsQueryListener {
            override fun onResponse(status: VehiclePickerStatus, versions: List<VehicleVersion>) {
                when (status){
                    SUCCESS -> {
                        itemVersions = buildItemsFromVersions(versions)
                        val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                        map[VERSIONS] = itemVersions
                        itemDataList.postValue(map)
                    }
                    //FAILED_TO_RETRIEVED_DATA ->
                    //_RESULT ->
                }
            }
        })
    }

    fun fetchVehicleCharacteristics(vehicleVersion: VehicleVersion){
        DriveKitVehiclePicker.getVehicle(vehicleVersion, object : VehicleDqVehicleQueryListener {
            override fun onResponse(status: VehiclePickerStatus, vehicleCharacteristics: VehicleCharacteristics) {
                // TODO check VehiclePickerStatus
                vehicleCharacteristicsData.postValue(vehicleCharacteristics)
            }
        })
    }
}

private fun shouldDisplayCategoryScreen(viewConfig: VehiclePickerViewConfig): Boolean {
    return when (viewConfig.categoryTypes) {
        CategoryType.LITE_CONFIG_ONLY,
        CategoryType.BOTH_CONFIG -> true
        CategoryType.BRANDS_CONFIG_ONLY -> false
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
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VehiclePickerViewModel() as T
    }
}