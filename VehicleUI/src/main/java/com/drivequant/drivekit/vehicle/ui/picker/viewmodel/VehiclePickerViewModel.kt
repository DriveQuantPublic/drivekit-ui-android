package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.DriveKitVehiclePicker
import com.drivequant.drivekit.vehicle.enums.TruckType
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.manager.*
import com.drivequant.drivekit.vehicle.picker.*
import com.drivequant.drivekit.vehicle.picker.VehiclePickerStatus.*
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
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
    var selectedTruckType: TruckType? = null
    lateinit var selectedCategory: VehicleCategoryItem
    lateinit var selectedBrand: VehicleBrand
    lateinit var selectedEngineIndex: VehicleEngineIndex
    lateinit var selectedModel: String
    lateinit var selectedYear: String
    lateinit var selectedVersion: VehicleVersion
    lateinit var name: String
    lateinit var carCharacteristics: CarCharacteristics
    lateinit var truckCharacteristics: TruckCharacteristics

    fun computeNextScreen(context: Context, currentStep: VehiclePickerStep?, otherAction: Boolean = false) {
        when (currentStep) {
            null -> {
                itemTypes = fetchVehicleTypes(context)
                if (itemTypes.size == 1) {
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
            TRUCK_TYPE -> {
                manageBrands(context)
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
                    manageEngineIndexes(context)
                } else {
                    itemBrands = fetchVehicleBrands(context)
                    stepDispatcher.postValue(BRANDS_FULL)
                }
            }
            BRANDS_FULL -> {
                manageEngineIndexes(context)

            }
            ENGINE -> fetchVehicleModels()
            MODELS -> fetchVehicleYears()
            YEARS -> fetchVehicleVersions()
            VERSIONS -> fetchVehicleCharacteristics()
            NAME -> {
                vehicleToDelete?.let {
                    replaceVehicle(it.vehicleId)
                } ?: run {
                    createVehicle()
                }
            }
        }
    }

    fun getItemsByStep(vehiclePickerStep: VehiclePickerStep): List<VehiclePickerItem> {
        return when (vehiclePickerStep) {
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
        for (i in vehicleTypesItems.indices) {
            val currentType = vehicleTypesItems[i]
            items.add(VehiclePickerItem(i, currentType.getTitle(context), currentType.name))
        }
        return items
    }

    private fun fetchTruckTypes(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val truckTypes = selectedVehicleTypeItem.getTruckTypes(context)
        for (i in truckTypes.indices) {
            items.add(VehiclePickerItem(i, truckTypes[i].title, truckTypes[i].truckType, truckTypes[i].icon))
        }
        return items
    }

    private fun fetchVehicleCategories(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val categories = selectedVehicleTypeItem.getCategories(context)
        for (i in categories.indices) {
            items.add(VehiclePickerItem(i, categories[i].title, categories[i].category, categories[i].icon1, categories[i].icon2))
        }
        return items
    }

    private fun fetchVehicleBrands(context: Context, withIcons: Boolean = false): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = selectedVehicleTypeItem.getBrands(context, withIcons)
        for (i in rawBrands.indices) {
            items.add(VehiclePickerItem(i, rawBrands[i].brand.value, rawBrands[i].brand.name, rawBrands[i].icon))
        }
        if (withIcons) {
            if (rawBrands.isNotEmpty() && rawBrands.size < VehicleBrand.getBrands(selectedVehicleTypeItem.vehicleType).size) {
                items.add(VehiclePickerItem(items.size, DKResource.convertToString(context, "dk_vehicle_other_brands"), "OTHER_BRANDS"))
            }
        }
        return items
    }

    private fun fetchVehicleEngines(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = selectedVehicleTypeItem.getEngineIndexes(context)
        for (i in rawBrands.indices) {
            items.add(VehiclePickerItem(i, rawBrands[i].title, rawBrands[i].engine.toString()))
        }
        return items
    }

    private fun fetchVehicleModels() {
        progressBarObserver.postValue(true)
        when (selectedVehicleTypeItem.vehicleType) {
            VehicleType.CAR -> {
                DriveKitVehiclePicker.getCarModels(selectedBrand, selectedEngineIndex, object : VehicleModelsQueryListener {
                    override fun onResponse(status: VehiclePickerStatus, models: List<String>) {
                        manageModelsResponse(status, models)
                    }
                })
            }
            VehicleType.TRUCK -> {
                selectedTruckType?.let { truckType ->
                    DriveKitVehiclePicker.getTruckModels(truckType, selectedBrand, object : VehicleModelsQueryListener {
                        override fun onResponse(status: VehiclePickerStatus, models: List<String>) {
                            manageModelsResponse(status, models)
                        }
                    })
                }
            }
        }
    }

    private fun manageModelsResponse(status: VehiclePickerStatus, models: List<String>) {
        when (status) {
            SUCCESS -> {
                itemModels = buildItemsFromStrings(models)
                stepDispatcher.postValue(MODELS)
            }
            FAILED_TO_RETRIEVED_DATA -> fetchServiceErrorObserver.postValue(FAILED_TO_RETRIEVED_DATA)
            NO_RESULT -> fetchServiceErrorObserver.postValue(NO_RESULT)
        }
        progressBarObserver.postValue(false)
    }

    private fun fetchVehicleYears() {
        progressBarObserver.postValue(true)
        when (selectedVehicleTypeItem.vehicleType) {
            VehicleType.CAR -> {
                DriveKitVehiclePicker.getCarYears(selectedBrand, selectedEngineIndex, selectedModel, object : VehicleYearsQueryListener {
                    override fun onResponse(status: VehiclePickerStatus, years: List<String>) {
                        manageYearsResponse(status, years)
                    }
                })
            }
            VehicleType.TRUCK -> {
                selectedTruckType?.let {truckType ->
                    DriveKitVehiclePicker.getTruckYears(truckType, selectedBrand, selectedModel, object : VehicleYearsQueryListener {
                        override fun onResponse(status: VehiclePickerStatus, years: List<String>) {
                            manageYearsResponse(status, years)
                        }
                    })
                }
            }
        }
    }

    private fun manageYearsResponse(status: VehiclePickerStatus, years: List<String>) {
        when (status) {
            SUCCESS -> {
                itemYears = buildItemsFromStrings(years)
                stepDispatcher.postValue(YEARS)
            }
            FAILED_TO_RETRIEVED_DATA -> fetchServiceErrorObserver.postValue(FAILED_TO_RETRIEVED_DATA)
            NO_RESULT -> fetchServiceErrorObserver.postValue(NO_RESULT)
        }
        progressBarObserver.postValue(false)
    }

    private fun fetchVehicleVersions() {
        progressBarObserver.postValue(true)
        when (selectedVehicleTypeItem.vehicleType) {
            VehicleType.CAR -> {
                DriveKitVehiclePicker.getCarVersions(selectedBrand, selectedEngineIndex, selectedModel, selectedYear, object : VehicleVersionsQueryListener {
                    override fun onResponse(status: VehiclePickerStatus, versions: List<VehicleVersion>) {
                        manageVersionsResponse(status, versions)
                    }
                })
            }
            VehicleType.TRUCK -> {
                selectedTruckType?.let {truckType ->
                    DriveKitVehiclePicker.getTruckVersions(truckType, selectedBrand, selectedModel, selectedYear, object : VehicleVersionsQueryListener {
                        override fun onResponse(status: VehiclePickerStatus, versions: List<VehicleVersion>) {
                            manageVersionsResponse(status, versions)
                        }
                    })
                }
            }
        }
    }

    private fun manageVersionsResponse(status: VehiclePickerStatus, versions: List<VehicleVersion>) {
        when (status) {
            SUCCESS -> {
                itemVersions = buildItemsFromVersions(versions)
                stepDispatcher.postValue(VERSIONS)
            }
            FAILED_TO_RETRIEVED_DATA -> fetchServiceErrorObserver.postValue(FAILED_TO_RETRIEVED_DATA)
            NO_RESULT -> fetchServiceErrorObserver.postValue(NO_RESULT)
        }
        progressBarObserver.postValue(false)
    }

    private fun fetchVehicleCharacteristics() {
        progressBarObserver.postValue(true)
        when (selectedVehicleTypeItem.vehicleType){
            VehicleType.CAR -> {
                DriveKitVehiclePicker.getCarCharacteristics(selectedVersion, object : CarVehicleCharacteristicsQueryListener {
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
            VehicleType.TRUCK -> {
                DriveKitVehiclePicker.getTruckCharacteristics(selectedVersion, object : TruckVehicleCharacteristicsQueryListener {
                    override fun onResponse(status: VehiclePickerStatus, truckCharacteristics: TruckCharacteristics) {
                        when (status){
                            SUCCESS -> {
                                this@VehiclePickerViewModel.truckCharacteristics = truckCharacteristics
                                stepDispatcher.postValue(NAME)
                            }
                            FAILED_TO_RETRIEVED_DATA -> fetchServiceErrorObserver.postValue(FAILED_TO_RETRIEVED_DATA)
                            NO_RESULT -> fetchServiceErrorObserver.postValue(NO_RESULT)
                        }
                        progressBarObserver.postValue(false)
                    }
                })
            }
        }
    }

    private fun createVehicle() {
        progressBarObserver.postValue(true)

        DriveKitVehicle.getVehiclesOrderByNameAsc(object : VehicleListQueryListener {
            override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                val detectionMode = computeCreateVehicleDetectionMode(vehicles)

                when (selectedVehicleTypeItem.vehicleType) {
                    VehicleType.CAR -> {
                        DriveKitVehicle.createCarVehicle(carCharacteristics, name, detectionMode, object : VehicleCreateQueryListener {
                            override fun onResponse(status: VehicleManagerStatus, vehicle: Vehicle) {
                                manageCreateOrReplaceVehicleResponse(status == VehicleManagerStatus.SUCCESS, vehicle)
                            }
                        }, isLiteConfig)
                    }
                    VehicleType.TRUCK -> {
                        DriveKitVehicle.createTruckVehicle(truckCharacteristics, name, detectionMode, object : VehicleCreateQueryListener {
                            override fun onResponse(status: VehicleManagerStatus, vehicle: Vehicle) {
                                manageCreateOrReplaceVehicleResponse(status == VehicleManagerStatus.SUCCESS, vehicle)
                            }
                        })
                    }
                }
            }
        }, SynchronizationType.CACHE)
    }

    private fun replaceVehicle(oldVehicleId: String) {
        progressBarObserver.postValue(true)
        when (selectedVehicleTypeItem.vehicleType) {
            VehicleType.CAR -> {
                DriveKitVehicle.replaceCarVehicle(
                    oldVehicleId = oldVehicleId,
                    carCharacteristics = this.carCharacteristics,
                    name = this.name,
                    isLiteConfig = this.isLiteConfig
                ) {
                    manageCreateOrReplaceVehicleResponse(status == VehicleReplaceStatus.SUCCESS, vehicle)
                }
            }
            VehicleType.TRUCK -> {
                DriveKitVehicle.replaceTruckVehicle(
                    oldVehicleId = oldVehicleId,
                    carCharacteristics = this.carCharacteristics,
                    name = this.name,
                    isLiteConfig = this.isLiteConfig
                ) {
                    manageCreateOrReplaceVehicleResponse(status == VehicleReplaceStatus.SUCCESS, vehicle)
                }
            }
        }
    }

    private fun manageCreateOrReplaceVehicleResponse(status: Boolean, vehicle: Vehicle) {
        val state = if (status){
            this.vehicleToDelete = null
            this.createdVehicleId = vehicle.vehicleId
            SUCCESS
        } else {
            FAILED_TO_RETRIEVED_DATA
        }
        endObserver.postValue(state)
        progressBarObserver.postValue(false)
    }

    private fun manageBrands(context: Context) {
        if (DriveKitVehicleUI.brandsWithIcons) {
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
            if (itemBrands.size == 1) {
                selectedBrand = VehicleBrand.getEnumByName(itemBrands.first().value)
                itemEngines = fetchVehicleEngines(context)
                stepDispatcher.postValue(ENGINE)
            } else {
                stepDispatcher.postValue(BRANDS_FULL)
            }
        }
    }

    private fun manageEngineIndexes(context: Context) {
        itemEngines = fetchVehicleEngines(context)
        when {
            selectedVehicleTypeItem.vehicleType == VehicleType.TRUCK -> {
                selectedEngineIndex = VehicleEngineIndex.DIESEL
                fetchVehicleModels()
            }
            itemEngines.size == 1 -> {
                selectedEngineIndex = VehicleEngineIndex.getEnumByName(itemEngines.first().value)
                fetchVehicleModels()
            }
            else -> stepDispatcher.postValue(ENGINE)
        }
    }

    fun getDescription(context: Context, vehiclePickerStep: VehiclePickerStep) =
        when (vehiclePickerStep) {
            TYPE -> "dk_vehicle_type_selection_title"
            TRUCK_TYPE -> "dk_vehicle_category_truck_selection_title"
            BRANDS_FULL -> "dk_vehicle_brand_description"
            ENGINE -> "dk_vehicle_engine_description"
            MODELS -> "dk_vehicle_model_description"
            YEARS -> "dk_vehicle_year_description"
            VERSIONS -> "dk_vehicle_version_description"
            CATEGORY,
            CATEGORY_DESCRIPTION,
            BRANDS_ICONS,
            NAME -> null
        }?.let {
            DKResource.convertToString(context, it)
        }

    fun getDefaultVehicleName() = if (isLiteConfig) {
        selectedCategory.title
    } else {
        "${selectedBrand.value} $selectedModel ${selectedVersion.version}"
    }

    private fun buildVehicleTypesItems(): List<VehicleTypeItem> {
        val typesItem = mutableListOf<VehicleTypeItem>()
        for (type in DriveKitVehicleUI.vehicleTypes) {
            typesItem.add(VehicleTypeItem.getEnumByVehicleType(type))
        }
        return typesItem
    }

    private fun computeCreateVehicleDetectionMode(vehicles: List<Vehicle>): DetectionMode {
        val detectionModes = DriveKitVehicleUI.detectionModes
        return if (detectionModes.isEmpty()) {
            DetectionMode.DISABLED
        } else if (detectionModes.contains(DetectionMode.GPS)) {
            if (vehicles.map { it.detectionMode }.contains(DetectionMode.GPS)) {
                detectionModes.firstOrNull { it != DetectionMode.GPS } ?: DetectionMode.DISABLED
            } else {
                DetectionMode.GPS
            }
        } else {
            detectionModes.first()
        }
    }

    private fun buildItemsFromStrings(source: List<String>) : MutableList<VehiclePickerItem> {
        val list: MutableList<VehiclePickerItem> = mutableListOf()
        for (i in source.indices) {
            list.add(VehiclePickerItem(i, source[i], source[i]))
        }
        return list
    }

    private fun buildItemsFromVersions(source: List<VehicleVersion>) : MutableList<VehiclePickerItem> {
        val list: MutableList<VehiclePickerItem> = mutableListOf()
        for (i in source.indices) {
            list.add(VehiclePickerItem(i, source[i].version, source[i].dqIndex))
        }
        return list
    }

    class VehiclePickerViewModelFactory : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VehiclePickerViewModel() as T
        }
    }
}
