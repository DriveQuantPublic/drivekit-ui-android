package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import java.io.Serializable

class VehiclePickerViewModel: ViewModel(), Serializable {

    fun fetchDataByStep(context: Context,
                        vehiclePickerStep: VehiclePickerStep,
                        viewConfig: VehiclePickerViewConfig)
            : List<VehiclePickerItem> {
        return when (vehiclePickerStep){
            TYPE -> VehicleType.CAR.toVehiclePickerItem(context, viewConfig)
            else -> listOf()
        }
    }
}

class VehiclePickerViewModelFactory() : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VehiclePickerViewModel() as T
    }
}