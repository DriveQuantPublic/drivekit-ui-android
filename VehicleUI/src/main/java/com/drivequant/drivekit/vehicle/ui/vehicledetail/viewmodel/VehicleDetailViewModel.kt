package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.dbvehicleaccess.DbVehicleAccess
import java.io.Serializable

class VehicleDetailViewModel(private val vehicleId: String): ViewModel(), Serializable {

    var vehicle: Vehicle?

    init {
        vehicle = fetchVehicle()
    }

    private fun fetchVehicle(): Vehicle? {
        return DbVehicleAccess.findVehicle(vehicleId).executeOne()
    }


    class VehicleDetailViewModelFactory(private val vehicleId: String)
        : ViewModelProvider.NewInstanceFactory() {
        override fun <T: ViewModel?> create(modelClass: Class<T>): T {
            return VehicleDetailViewModel(vehicleId) as T
        }
    }
}