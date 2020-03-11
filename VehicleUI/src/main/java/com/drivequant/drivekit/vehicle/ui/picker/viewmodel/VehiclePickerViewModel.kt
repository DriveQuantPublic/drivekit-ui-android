package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.dbvehicleaccess.DbVehicleAccess
import com.drivequant.drivekit.vehicle.DriveKitVehicleManager
import com.drivequant.drivekit.vehicle.DriveKitVehiclePicker
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.manager.VehicleCreateQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleDeleteQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleManagerStatus
import com.drivequant.drivekit.vehicle.picker.*
import com.drivequant.drivekit.vehicle.picker.VehiclePickerStatus.*
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import java.io.Serializable

class VehiclePickerViewModel: ViewModel(), Serializable {
    val stepDispatcher = MutableLiveData<VehiclePickerStep>()
    val endObserver = MutableLiveData<Any>()
    val progressBarObserver = MutableLiveData<Boolean>()
    val fetchServiceErrorObserver = MutableLiveData<VehiclePickerStatus>()

    private var itemTypes = listOf<VehiclePickerItem>()
    private var itemCategories = listOf<VehiclePickerItem>()
    private var itemBrands = listOf<VehiclePickerItem>()
    private var itemEngines = listOf<VehiclePickerItem>()
    private var itemModels = listOf<VehiclePickerItem>()
    private var itemYears = listOf<VehiclePickerItem>()
    private var itemVersions = listOf<VehiclePickerItem>()

    private var isLiteConfig = false

    var vehicleToDelete: Vehicle? = null

    lateinit var selectedVehicleTypeItem: VehicleTypeItem
    lateinit var selectedCategory: VehicleCategoryItem
    lateinit var selectedBrand: VehicleBrand
    lateinit var selectedEngineIndex: VehicleEngineIndex
    lateinit var selectedModel: String
    lateinit var selectedYear: String
    lateinit var selectedVersion: VehicleVersion
    lateinit var name: String
    lateinit var characteristics: VehicleCharacteristics

    fun computeNextScreen(context: Context, currentStep: VehiclePickerStep?, viewConfig: VehiclePickerViewConfig, otherAction: Boolean = false){
        when (currentStep){
            null -> {
                itemTypes = fetchVehicleTypes(context, viewConfig)
                if (itemTypes.size == 1){
                    selectedVehicleTypeItem = VehicleTypeItem.valueOf(itemTypes.first().value)
                    computeNextScreen(context, TYPE, viewConfig)
                } else {
                    stepDispatcher.postValue(TYPE)
                }
            }
            TYPE -> {
                if (viewConfig.categoryConfigTypes != CategoryConfigType.BRANDS_CONFIG_ONLY) {
                    itemCategories = fetchVehicleCategories(context)
                    stepDispatcher.postValue(CATEGORY)
                } else {
                    manageBrands(context, viewConfig)
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
                    manageBrands(context, viewConfig)
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
        for (i in viewConfig.vehicleTypeItems.indices){
            val currentType = viewConfig.vehicleTypeItems[i]
            items.add(VehiclePickerItem(i, currentType.getTitle(context), currentType.name))
        }
        return items
    }

    private fun fetchVehicleCategories(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawCategories = selectedVehicleTypeItem.getCategories(context)
        for (i in rawCategories.indices){
            items.add(VehiclePickerItem(i, rawCategories[i].title, rawCategories[i].category, rawCategories[i].icon1, rawCategories[i].icon2))
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
            if (selectedVehicleTypeItem.getBrands(context).isNotEmpty()){
                items.add(VehiclePickerItem(items.size, context.getString(R.string.dk_vehicle_other_brands), "OTHER_BRANDS"))
            }
        }
        return items
    }

    private fun fetchVehicleEngines(context: Context): List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = selectedVehicleTypeItem.getEngines(context)
        for (i in rawBrands.indices){
            items.add(VehiclePickerItem(i, rawBrands[i].title, rawBrands[i].engine.toString()))
        }
        return items
    }

    private fun fetchVehicleModels() {
        progressBarObserver.postValue(true)
        DriveKitVehiclePicker.getModels(selectedBrand, selectedEngineIndex, object : VehicleModelsQueryListener{
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
        DriveKitVehiclePicker.getYears(selectedBrand, selectedEngineIndex, selectedModel, object : VehicleYearsQueryListener {
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
        DriveKitVehiclePicker.getVersions(selectedBrand, selectedEngineIndex, selectedModel, selectedYear, object : VehicleVersionsQueryListener {
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
        DriveKitVehiclePicker.getCharacteristics(selectedVersion, object : VehicleCharacteristicsQueryListener {
            override fun onResponse(status: VehiclePickerStatus, vehicleCharacteristics: VehicleCharacteristics) {
                when (status){
                    SUCCESS -> {
                        characteristics = vehicleCharacteristics
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
        val detectionMode = computeCreateVehicleDetectionMode(listOf())
        DriveKitVehicleManager.createVehicle(characteristics, name, detectionMode, object: VehicleCreateQueryListener{
            override fun onResponse(status: VehicleManagerStatus, vehicle: Vehicle) {
                if (status == VehicleManagerStatus.SUCCESS){
                    vehicleToDelete?.let {
                        DriveKitVehicleManager.deleteVehicle(it, object: VehicleDeleteQueryListener {
                            override fun onResponse(status: VehicleManagerStatus) {
                                vehicleToDelete = null
                                endObserver.postValue(null)
                                progressBarObserver.postValue(false)
                            }
                        })
                    }?: run {
                        endObserver.postValue(null)
                        progressBarObserver.postValue(false)
                    }
                } else {
                    fetchServiceErrorObserver.postValue(FAILED_TO_RETRIEVED_DATA)
                    progressBarObserver.postValue(false)
                }
            }
        })
    }

    private fun manageBrands(context: Context, viewConfig: VehiclePickerViewConfig){
        if (viewConfig.displayBrandsWithIcons){
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
        return when (vehiclePickerStep){
            BRANDS_FULL -> context.getString(R.string.dk_vehicle_brand_description)
            ENGINE -> context.getString(R.string.dk_vehicle_engine_description)
            MODELS -> context.getString(R.string.dk_vehicle_model_description)
            YEARS -> context.getString(R.string.dk_vehicle_year_description)
            VERSIONS -> context.getString(R.string.dk_vehicle_version_description)
            else -> null
        }
    }

    fun getDefaultVehicleName(): String? {
        return if (isLiteConfig){
            selectedCategory.title
        } else {
            "$selectedBrand $selectedModel ${selectedVersion.version}"
        }
    }

    private fun computeCreateVehicleDetectionMode(detectionModes: List<DetectionMode>): DetectionMode {
        return if (detectionModes.isEmpty()){
            DetectionMode.DISABLED
        } else if (detectionModes.contains(DetectionMode.GPS)){
            if (DbVehicleAccess.findVehiclesByDetectionMode(DetectionMode.GPS).execute().isEmpty()){
                DetectionMode.GPS
            } else {
                detectionModes[0]
            }
        } else {
            detectionModes[0]
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