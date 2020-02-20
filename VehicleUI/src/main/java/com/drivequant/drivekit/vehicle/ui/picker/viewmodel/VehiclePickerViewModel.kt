package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.vehicle.DriveKitVehicleManager
import com.drivequant.drivekit.vehicle.DriveKitVehiclePicker
import com.drivequant.drivekit.vehicle.enum.VehicleBrand
import com.drivequant.drivekit.vehicle.enum.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.manager.VehicleManagerStatus
import com.drivequant.drivekit.vehicle.picker.*
import com.drivequant.drivekit.vehicle.picker.VehiclePickerStatus.*
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import java.io.Serializable

class VehiclePickerViewModel: ViewModel(), Serializable {
    var retItems: MutableList<VehiclePickerItem> = mutableListOf()
    val itemsData: MutableLiveData<List<VehiclePickerItem>> = MutableLiveData()
    var status: VehicleManagerStatus = VehicleManagerStatus.SUCCESS

    fun fetchVehicleTypes(context: Context, viewConfig: VehiclePickerViewConfig) {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        for (i in viewConfig.vehicleTypes.indices){
            val currentType = viewConfig.vehicleTypes[i]
            items.add(i, VehiclePickerItem(i, currentType.getTitle(context), currentType.name))
        }
        itemsData.postValue(items)
    }

    fun fetchVehicleCategories(context: Context, vehicleType: VehicleType) {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawCategories = vehicleType.getCategories(context)
        for (i in rawCategories.indices){
            items.add(i, VehiclePickerItem(i, rawCategories[i].title, rawCategories[i].category, rawCategories[i].icon1, rawCategories[i].icon2))
        }
        itemsData.postValue(items)
    }

    fun fetchVehicleBrands(context: Context, vehicleType: VehicleType, withIcons: Boolean = false) {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = vehicleType.getBrands(context, withIcons)
        for (i in rawBrands.indices){
            items.add(i, VehiclePickerItem(i, rawBrands[i].brand.value, rawBrands[i].brand.name, rawBrands[i].icon))
        }
        itemsData.postValue(items)
    }

    fun fetchVehicleEngines(context: Context, vehicleType: VehicleType) {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = vehicleType.getEngines(context)
        for (i in rawBrands.indices){
            items.add(i, VehiclePickerItem(i, rawBrands[i].title, rawBrands[i].engine.toString()))
        }
        itemsData.postValue(items)
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
                itemsData.postValue(items)
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
                itemsData.postValue(items)
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
                itemsData.postValue(items)
            }
        })
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