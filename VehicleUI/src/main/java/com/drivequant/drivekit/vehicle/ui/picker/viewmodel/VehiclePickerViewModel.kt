package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.DriveKitVehiclePicker
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.manager.*
import com.drivequant.drivekit.vehicle.picker.*
import com.drivequant.drivekit.vehicle.picker.VehiclePickerStatus.*
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleCategoryItem
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import java.io.Serializable

class VehiclePickerViewModel: ViewModel(), Serializable {
    val stepDispatcher = MutableLiveData<VehiclePickerStep>()
    val endObserver = MutableLiveData<VehiclePickerStatus>()
    val progressBarObserver = MutableLiveData<Boolean>()
    val fetchServiceErrorObserver = MutableLiveData<VehiclePickerStatus>()

    private var itemTypes = listOf<VehiclePickerItem>()
    private var itemTruckTypes = listOf<VehiclePickerItem>()
    private var itemCategories = listOf<VehiclePickerItem>()
    private var itemBrands = listOf<VehiclePickerItem>()
    private var itemEngines = listOf<VehiclePickerItem>()
    private var itemModels = listOf<VehiclePickerItem>()
    private var itemYears = listOf<VehiclePickerItem>()
    private var itemVersions = listOf<VehiclePickerItem>()

    private var isLiteConfig = false

    var vehicleToDelete: Vehicle? = null
    var createdVehicleId: String? = null

    lateinit var selectedVehicleTypeItem: VehicleTypeItem
    lateinit var selectedCategory: VehicleCategoryItem
    lateinit var selectedBrand: VehicleBrand
    lateinit var selectedEngineIndex: VehicleEngineIndex
    lateinit var selectedModel: String
    lateinit var selectedYear: String
    lateinit var selectedVersion: VehicleVersion
    lateinit var name: String
    lateinit var carCharacteristics: CarCharacteristics

    fun computeNextScreen(context: Context, currentStep: VehiclePickerStep?, otherAction: Boolean = false){
        when (currentStep){
            null -> {
                itemTypes = fetchVehicleTypes(context)
                if (itemTypes.size == 1){
                    selectedVehicleTypeItem = VehicleTypeItem.valueOf(itemTypes.first().value)
                    computeNextScreen(context, TYPE)
                } else {
                    stepDispatcher.postValue(TYPE)
                }
            }
            TYPE -> {
                if (selectedVehicleTypeItem.vehicleType == VehicleType.CAR) {
                    if (DriveKitVehicleUI.categoryConfigType != CategoryConfigType.BRANDS_CONFIG_ONLY) {
                        itemCategories = fetchVehicleCategories(context)
                        stepDispatcher.postValue(CATEGORY)
                    } else {
                        manageBrands(context)
                    }
                } else {
                    itemTruckTypes = fetchTruckTypes(context)
                    stepDispatcher.postValue(TRUCK_TYPE)
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
                    manageBrands(context)
                }
            }
            BRANDS_ICONS -> {
                if (!otherAction) {
                    itemEngines = fetchVehicleEngines(context)
                    stepDispatcher.postValue(ENGINE)
                } else {
                    itemBrands = fetchVehicleBrands(context)
                    stepDispatcher.postValue(BRANDS_FULL)
                }
            }
            BRANDS_FULL -> {
                itemEngines = fetchVehicleEngines(context)
                stepDispatcher.postValue(ENGINE)
            }
            ENGINE -> fetchVehicleModels()
            MODELS -> fetchVehicleYears()
            YEARS -> fetchVehicleVersions()
            VERSIONS -> fetchVehicleCharacteristics()
            NAME -> createVehicle()
        }
    }

    fun getItemsByStep(vehiclePickerStep: VehiclePickerStep): List<VehiclePickerItem> {
        return when (vehiclePickerStep){
            TYPE -> itemTypes
            TRUCK_TYPE -> itemTruckTypes
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

    private fun fetchVehicleTypes(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val vehicleTypesItems = buildVehicleTypesItems()
        for (i in vehicleTypesItems.indices){
            val currentType = vehicleTypesItems[i]
            items.add(VehiclePickerItem(i, currentType.getTitle(context), currentType.name))
        }
        return items
    }

    private fun fetchTruckTypes(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val truckTypes = selectedVehicleTypeItem.getTruckTypes(context)
        for (i in truckTypes.indices){
            items.add(VehiclePickerItem(i, truckTypes[i].title, truckTypes[i].truckType, truckTypes[i].icon))
        }
        return items
    }

    private fun fetchVehicleCategories(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val categories = selectedVehicleTypeItem.getCategories(context)
        for (i in categories.indices){
            items.add(VehiclePickerItem(i, categories[i].title, categories[i].category, categories[i].icon1, categories[i].icon2))
        }
        return items
    }

    private fun fetchVehicleBrands(context: Context, withIcons: Boolean = false): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = selectedVehicleTypeItem.getBrands(context, withIcons)
        for (i in rawBrands.indices){
            items.add(VehiclePickerItem(i, rawBrands[i].brand.value, rawBrands[i].brand.name, rawBrands[i].icon))
        }
        if (withIcons){
            if (rawBrands.isNotEmpty() && rawBrands.size < VehicleBrand.values().size){
                items.add(
                    VehiclePickerItem(
                        items.size,
                        DKResource.convertToString(context, "dk_vehicle_other_brands"),
                        "OTHER_BRANDS"))
            }
        }
        return items
    }

    private fun fetchVehicleEngines(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = selectedVehicleTypeItem.getEngineIndexes(context)
        for (i in rawBrands.indices){
            items.add(VehiclePickerItem(i, rawBrands[i].title, rawBrands[i].engine.toString()))
        }
        return items
    }

    private fun fetchVehicleModels() {
        progressBarObserver.postValue(true)
        DriveKitVehiclePicker.getCarModels(selectedBrand, selectedEngineIndex, object : VehicleModelsQueryListener{
            override fun onResponse(status: VehiclePickerStatus, models: List<String>) {
                when (status){
                    SUCCESS -> {
                        itemModels = buildItemsFromStrings(models)
                        stepDispatcher.postValue(MODELS)
                    }
                    FAILED_TO_RETRIEVED_DATA -> fetchServiceErrorObserver.postValue(FAILED_TO_RETRIEVED_DATA)
                    NO_RESULT -> fetchServiceErrorObserver.postValue(NO_RESULT)
                }
                progressBarObserver.postValue(false)
            }
        })
    }

    private fun fetchVehicleYears() {
        progressBarObserver.postValue(true)
        DriveKitVehiclePicker.getCarYears(selectedBrand, selectedEngineIndex, selectedModel, object : VehicleYearsQueryListener {
            override fun onResponse(status: VehiclePickerStatus, years: List<String>) {
                when (status){
                    SUCCESS -> {
                        itemYears = buildItemsFromStrings(years)
                        stepDispatcher.postValue(YEARS)
                    }
                    FAILED_TO_RETRIEVED_DATA -> fetchServiceErrorObserver.postValue(FAILED_TO_RETRIEVED_DATA)
                    NO_RESULT -> fetchServiceErrorObserver.postValue(NO_RESULT)
                }
                progressBarObserver.postValue(false)
            }
        })
    }

    private fun fetchVehicleVersions() {
        progressBarObserver.postValue(true)
        DriveKitVehiclePicker.getCarVersions(selectedBrand, selectedEngineIndex, selectedModel, selectedYear, object : VehicleVersionsQueryListener {
            override fun onResponse(status: VehiclePickerStatus, versions: List<VehicleVersion>) {
                when (status){
                    SUCCESS -> {
                        itemVersions = buildItemsFromVersions(versions)
                        stepDispatcher.postValue(VERSIONS)
                    }
                    FAILED_TO_RETRIEVED_DATA -> fetchServiceErrorObserver.postValue(FAILED_TO_RETRIEVED_DATA)
                    NO_RESULT -> fetchServiceErrorObserver.postValue(NO_RESULT)
                }
                progressBarObserver.postValue(false)
            }
        })
    }

    private fun fetchVehicleCharacteristics(){
        progressBarObserver.postValue(true)
        DriveKitVehiclePicker.getCarCharacteristics(selectedVersion, object : VehicleCarCharacteristicsQueryListener {
            override fun onResponse(status: VehiclePickerStatus, carCharacteristics: CarCharacteristics) {
                when (status){
                    SUCCESS -> {
                        this@VehiclePickerViewModel.carCharacteristics = carCharacteristics
                        stepDispatcher.postValue(NAME)
                    }
                    FAILED_TO_RETRIEVED_DATA -> fetchServiceErrorObserver.postValue(FAILED_TO_RETRIEVED_DATA)
                    NO_RESULT -> fetchServiceErrorObserver.postValue(NO_RESULT)
                }
                progressBarObserver.postValue(false)
            }
        })
    }

    private fun createVehicle(){
        progressBarObserver.postValue(true)

        DriveKitVehicle.getVehiclesOrderByNameAsc(object : VehicleListQueryListener {
            override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                val detectionMode = computeCreateVehicleDetectionMode(vehicles)
                DriveKitVehicle.createCarVehicle(carCharacteristics, name, detectionMode, object: VehicleCreateQueryListener{
                    override fun onResponse(status: VehicleManagerStatus, vehicle: Vehicle) {
                        if (status == VehicleManagerStatus.SUCCESS){
                            vehicleToDelete?.let {
                                DriveKitVehicle.deleteVehicle(it, object: VehicleDeleteQueryListener {
                                    override fun onResponse(status: VehicleManagerStatus) {
                                        vehicleToDelete = null
                                        createdVehicleId = vehicle.vehicleId
                                        if (status == VehicleManagerStatus.SUCCESS) {
                                            endObserver.postValue(SUCCESS)
                                        } else {
                                            endObserver.postValue(FAILED_TO_RETRIEVED_DATA)
                                        }
                                        progressBarObserver.postValue(false)
                                    }
                                })
                            }?: run {
                                createdVehicleId = vehicle.vehicleId
                                endObserver.postValue(SUCCESS)
                                progressBarObserver.postValue(false)
                            }
                        } else {
                            endObserver.postValue(FAILED_TO_RETRIEVED_DATA)
                            progressBarObserver.postValue(false)
                        }
                    }
                }, isLiteConfig)
            }
        }, SynchronizationType.CACHE)
    }

    private fun manageBrands(context: Context){
        if (DriveKitVehicleUI.brandsWithIcons){
            itemBrands = fetchVehicleBrands(context, withIcons = true)
            if (itemBrands.size == 1){
                selectedBrand = VehicleBrand.getEnumByName(itemBrands.first().value)
                itemEngines = fetchVehicleEngines(context)
                stepDispatcher.postValue(ENGINE)
            } else {
                stepDispatcher.postValue(BRANDS_ICONS)
            }
        } else {
            itemBrands = fetchVehicleBrands(context)
            if (itemBrands.size == 1){
                selectedBrand = VehicleBrand.getEnumByName(itemBrands.first().value)
                itemEngines = fetchVehicleEngines(context)
                stepDispatcher.postValue(ENGINE)
            } else {
                stepDispatcher.postValue(BRANDS_FULL)
            }
        }
    }

    fun getDescription(context: Context, vehiclePickerStep: VehiclePickerStep): String? {
        val stringResId = when (vehiclePickerStep){
            TYPE -> "dk_vehicle_type_selection_title"
            BRANDS_FULL -> "dk_vehicle_brand_description"
            ENGINE -> "dk_vehicle_engine_description"
            MODELS -> "dk_vehicle_model_description"
            YEARS -> "dk_vehicle_year_description"
            VERSIONS -> "dk_vehicle_version_description"
            else -> null
        }
        stringResId?.let {
            return DKResource.convertToString(context, it)
        }?: run {
            return null
        }
    }

    fun getDefaultVehicleName(): String? {
        return if (isLiteConfig){
            selectedCategory.title
        } else {
            "${selectedBrand.value} $selectedModel ${selectedVersion.version}"
        }
    }

    private fun buildVehicleTypesItems(): List<VehicleTypeItem> {
        val typesItem = mutableListOf<VehicleTypeItem>()
        for (type in DriveKitVehicleUI.vehicleTypes){
            typesItem.add(VehicleTypeItem.getEnumByVehicleType(type))
        }
        return typesItem
    }

    private fun computeCreateVehicleDetectionMode(vehicles: List<Vehicle>): DetectionMode {
        val detectionModes = DriveKitVehicleUI.detectionModes
        return if (detectionModes.isEmpty()){
            DetectionMode.DISABLED
        } else if (detectionModes.contains(DetectionMode.GPS)){
            if (vehicles.isEmpty()){
                DetectionMode.GPS
            } else {
                detectionModes.first()
            }
        } else {
            detectionModes.first()
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