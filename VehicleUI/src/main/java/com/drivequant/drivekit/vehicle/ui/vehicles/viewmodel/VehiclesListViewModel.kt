package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicleManager
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.computeSubtitle
import com.drivequant.drivekit.vehicle.ui.extension.computeTitle
import java.io.Serializable

class VehiclesListViewModel : ViewModel(), Serializable {
    val vehiclesData = MutableLiveData<List<Vehicle>>()
    var vehiclesList: List<Vehicle> = listOf()
    var syncStatus: VehicleSyncStatus = VehicleSyncStatus.NO_ERROR

    fun fetchVehicles(synchronizationType: SynchronizationType = SynchronizationType.DEFAULT){
        if (DriveKit.isConfigured()) {
            DriveKitVehicleManager.getVehiclesOrderByNameAsc(object : VehicleListQueryListener {
                override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                    syncStatus = status
                    vehiclesList = vehicles
                    vehiclesData.postValue(vehiclesList)
                }
            }, synchronizationType)
        } else {
            vehiclesData.postValue(listOf())
        }
    }

    fun getScreenTitle(context: Context?): String? {
        return if (vehiclesList.size > 1){
            context?.getString(R.string.dk_vehicle_my_vehicles)
        } else {
            context?.getString(R.string.dk_vehicle_my_vehicle)
        }
    }

    fun getTitle(context: Context, vehicle: Vehicle): String {
        return vehicle.computeTitle(context, vehiclesList)
    }

    fun getSubtitle(context: Context, vehicle: Vehicle): String? {
        return vehicle.computeSubtitle(context, vehiclesList)
    }
}