package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicleManager
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.computeTitle
import java.io.Serializable

class VehiclesListViewModel : ViewModel(), Serializable {
    val vehiclesData = MutableLiveData<List<Vehicle>>()
    private var vehiclesList: List<Vehicle> = listOf()
    var syncStatus: VehicleSyncStatus = VehicleSyncStatus.NO_ERROR

    fun fetchVehicles(){
        if (DriveKit.isConfigured()) { // TODO if DriveKitVehicle.isConfigured() ?
            DriveKitVehicleManager.getVehiclesOrderByNameAsc(object : VehicleListQueryListener {
                override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                    syncStatus = status
                    vehiclesList = vehicles
                    vehiclesData.postValue(vehiclesList)
                }
            }, SynchronizationType.DEFAULT)
        } else {
            vehiclesData.postValue(listOf())
        }
    }

    fun getTitle(context: Context, vehicle: Vehicle): String {
        return vehicle.computeTitle(context)
    }

    fun getSubtitle(context: Context, vehicle: Vehicle): String {
        // Todo if liteconfig, display category by carTypeIndex ?
        return vehicle.computeTitle(context)
    }

    class VehicleListViewModelFactory : ViewModelProvider.NewInstanceFactory(){
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return VehiclesListViewModel() as T
        }
    }
}