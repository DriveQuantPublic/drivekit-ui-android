package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleType.*
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
            items.add(i, VehiclePickerItem(i, rawCategories[i].getTitle(context), vehicleType.name))
        }
        return items
    }
}

class VehiclePickerViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VehiclePickerViewModel() as T
    }
}