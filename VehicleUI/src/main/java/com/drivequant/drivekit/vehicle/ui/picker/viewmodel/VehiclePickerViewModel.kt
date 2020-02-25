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
    val itemListData: MutableLiveData<List<VehiclePickerItem>> = MutableLiveData()
    val itemMapNewData: MutableLiveData<Map<VehiclePickerStep, List<VehiclePickerItem>>> = MutableLiveData()
    val vehicleCharacteristicsData: MutableLiveData<VehicleCharacteristics> = MutableLiveData()

    var itemTypes: List<VehiclePickerItem> = listOf()
    var itemCategories: List<VehiclePickerItem> = listOf()
    var itemBrands: List<VehiclePickerItem> = listOf()
    var itemEngines: List<VehiclePickerItem> = listOf()

    lateinit var selectedVehicleType: VehicleType
    lateinit var selectedCategory: VehicleCategoryItem
    lateinit var selectedBrand: VehicleBrand
    lateinit var selectedEngine: VehicleEngineItem

    fun computeNextScreen(context: Context, currentStep: VehiclePickerStep?, viewConfig: VehiclePickerViewConfig){
        when (currentStep){
            TYPE -> {
                itemCategories = fetchVehicleCategories(context, selectedVehicleType)
                val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                map[CATEGORY] = itemCategories
                itemMapNewData.postValue(map)
            }
            CATEGORY -> {
                itemBrands = fetchVehicleBrands(context, selectedVehicleType, withIcons = false)
                val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                map[BRANDS_FULL] = itemBrands
                itemMapNewData.postValue(map)
            }
            CATEGORY_DESCRIPTION -> TODO()
            BRANDS_ICONS, BRANDS_FULL -> {
                itemEngines = fetchVehicleEngines(context, selectedVehicleType)
                val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                map[ENGINE] = itemEngines
                itemMapNewData.postValue(map)
            }
            ENGINE -> TODO()
            MODELS -> TODO()
            YEARS -> TODO()
            VERSIONS -> TODO()
            NAME -> TODO()
            null -> {
                itemTypes = fetchVehicleTypes(context, viewConfig)
                val map = HashMap<VehiclePickerStep, List<VehiclePickerItem>>()
                map[TYPE] = itemTypes
                itemMapNewData.postValue(map)
            }
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

    fun fetchVehicleModels(vehicleType: VehicleType, vehicleBrand: VehicleBrand, vehicleEngineIndex: VehicleEngineIndex) {
        DriveKitVehiclePicker.getModels(vehicleBrand, vehicleEngineIndex, object : VehicleModelsQueryListener{
            override fun onResponse(status: VehiclePickerStatus, models: List<String>) {
                val items = when (status){
                    SUCCESS -> {
                        buildItemsFromStrings(models)
                    }
                    FAILED_TO_RETRIEVED_DATA -> mutableListOf()
                    NO_RESULT -> mutableListOf()
                }
                itemListData.postValue(items)
            }
        })
    }

    fun fetchVehicleYears(vehicleType: VehicleType, vehicleBrand: VehicleBrand, vehicleEngineIndex: VehicleEngineIndex, vehicleModel: String) {
        DriveKitVehiclePicker.getYears(vehicleBrand, vehicleEngineIndex, vehicleModel, object : VehicleYearsQueryListener {
            override fun onResponse(status: VehiclePickerStatus, years: List<String>) {
                val items = when (status){
                    SUCCESS -> {
                        buildItemsFromStrings(years)
                    }
                    FAILED_TO_RETRIEVED_DATA -> mutableListOf()
                    NO_RESULT -> mutableListOf()
                }
                itemListData.postValue(items)
            }
        })
    }

    fun fetchVehicleVersions(vehicleType: VehicleType, vehicleBrand: VehicleBrand, vehicleEngineIndex: VehicleEngineIndex, vehicleModel: String, vehicleYear: String) {
        DriveKitVehiclePicker.getVersions(vehicleBrand, vehicleEngineIndex, vehicleModel, vehicleYear, object : VehicleVersionsQueryListener {
            override fun onResponse(status: VehiclePickerStatus, versions: List<VehicleVersion>) {
                val items = when (status){
                    SUCCESS -> {
                        buildItemsFromVersions(versions)
                    }
                    FAILED_TO_RETRIEVED_DATA -> mutableListOf()
                    NO_RESULT -> mutableListOf()
                }
                itemListData.postValue(items)
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