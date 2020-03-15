package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.dbvehicleaccess.DbVehicleAccess
import java.io.Serializable

class VehicleDetailViewModel(private val vehicleId: String): ViewModel(), Serializable {

    var imageFilePath: String? = null
    var vehicle: Vehicle?
    lateinit var groupFields: List<GroupField>

    init {
        vehicle = fetchVehicle()
        createGroupFields()
    }

    private fun fetchVehicle(): Vehicle? {
        return DbVehicleAccess.findVehicle(vehicleId).executeOne()
    }

    private fun createGroupFields() {
        groupFields = listOf(GroupField.GENERAL, GroupField.BEACON) // TODO mock
    }

    class VehicleDetailViewModelFactory(private val vehicleId: String)
        : ViewModelProvider.NewInstanceFactory() {
        override fun <T: ViewModel?> create(modelClass: Class<T>): T {
            return VehicleDetailViewModel(vehicleId) as T
        }
    }
}