package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.DetectionMode.*
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

    fun getScreenTitle(context: Context?): String? {
        return if (vehiclesList.size > 1){
            context?.getString(R.string.dk_vehicle_my_vehicles)
        } else {
            context?.getString(R.string.dk_vehicle_my_vehicle)
        }
    }

    fun getTitle(context: Context, vehicle: Vehicle): String {
        return vehicle.computeTitle(context)
    }

    fun getSubtitle(context: Context, vehicle: Vehicle): String {
        return vehicle.computeSubtitle(context)
    }

    fun getDetectionModeDescription(context: Context, vehicle: Vehicle): String {
        // TODO when not configured, display imageview & text is in bold & red
        return when (vehicle.detectionMode) {
            DISABLED -> {
                context.getString(R.string.dk_detection_mode_disabled_desc)
            }
            GPS -> {
                context.getString(R.string.dk_detection_mode_gps_desc)
            }
            BEACON -> {
                vehicle.beacon?.let {
                    context.getString(R.string.dk_detection_mode_beacon_desc_configured, it.code)
                } ?: run {
                    context.getString(R.string.dk_detection_mode_beacon_desc_not_configured)
                }
            }
            BLUETOOTH -> {
                vehicle.bluetooth?.let {
                    context.getString(R.string.dk_detection_mode_bluetooth_desc_configured, it.name)
                } ?: run {
                    context.getString(R.string.dk_detection_mode_bluetooth_desc_not_configured)
                }
            }
        }
    }

    class VehicleListViewModelFactory : ViewModelProvider.NewInstanceFactory(){
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return VehiclesListViewModel() as T
        }
    }
}