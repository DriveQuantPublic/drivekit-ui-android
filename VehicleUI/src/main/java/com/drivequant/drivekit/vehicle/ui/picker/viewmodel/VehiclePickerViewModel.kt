package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.vehicle.enum.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import java.io.Serializable

class VehiclePickerViewModel: ViewModel(), Serializable {

    fun fetchVehicleTypes(
        context: Context,
        viewConfig: VehiclePickerViewConfig
    ) : List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        for (i in viewConfig.vehicleTypes.indices){
            val currentType = viewConfig.vehicleTypes[i]
            items.add(i, VehiclePickerItem(i, currentType.getTitle(context), currentType.name))
        }
        return items
    }

    fun fetchVehicleCategories(
        context: Context,
        vehicleType: VehicleType
    ) : List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawCategories = vehicleType.getCategories()
        for (i in rawCategories.indices){
            items.add(i, VehiclePickerItem(i, rawCategories[i].getTitle(context), rawCategories[i].name, rawCategories[i].getIcon1(context), rawCategories[i].getIcon2(context)))
        }
        return items
    }

    fun fetchVehicleBrands(
        context: Context,
        vehicleType: VehicleType,
        withIcons: Boolean = false
    ) : List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawBrands = vehicleType.getBrands(context, withIcons)
        for (i in rawBrands.indices){
            items.add(i, VehiclePickerItem(i, rawBrands[i].brand.value, rawBrands[i].brand.name, rawBrands[i].icon))
        }
        return items
    }

    fun fetchVehicleEngines(context: Context) : List<VehiclePickerItem> {
        val items: MutableList<VehiclePickerItem> = mutableListOf()
        val rawEngines = VehicleEngineIndex.values()
        for (i in rawEngines.indices){
            items.add(i, VehiclePickerItem(i, rawEngines[i].name, rawEngines[i].value.toString()))
        }
        return items
    }
}

class VehiclePickerViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VehiclePickerViewModel() as T
    }
}