package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.vehicledetail.common.EditableField
import java.io.Serializable

class VehicleDetailViewModel(private val vehicleId: String): ViewModel(), Serializable {

    val progressBarObserver = MutableLiveData<Boolean>()
    var newEditableFieldObserver = MutableLiveData<EditableField>()

    var vehicle: Vehicle? = null
    var vehicleName: String? = null
    var groupFields: MutableList<GroupField> = mutableListOf()

    fun init(context: Context){
        vehicle = DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()
        vehicle?.let {
            vehicleName = it.buildFormattedName(context)
            createGroupFields()
        }
    }

    private fun createGroupFields() {
        vehicle?.let {
            groupFields.clear()
            for (groupField in GroupField.getGroupFields(it)){
                if (groupField.isDisplayable(it)){
                    groupFields.add(groupField)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class VehicleDetailViewModelFactory(private val vehicleId: String)
        : ViewModelProvider.NewInstanceFactory() {
        override fun <T: ViewModel?> create(modelClass: Class<T>): T {
            return VehicleDetailViewModel(vehicleId) as T
        }
    }
}