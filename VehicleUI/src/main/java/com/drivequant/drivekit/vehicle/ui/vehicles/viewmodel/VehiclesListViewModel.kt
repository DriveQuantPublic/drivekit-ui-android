package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconRemoveStatus
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleRemoveBeaconQueryListener
import com.drivequant.drivekit.vehicle.manager.bluetooth.VehicleRemoveBluetoothQueryListener
import com.drivequant.drivekit.vehicle.manager.bluetooth.VehicleRemoveBluetoothStatus
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.extension.computeSubtitle
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import com.drivequant.drivekit.vehicle.ui.vehicles.viewholder.DetectionModeSpinnerItem
import java.io.Serializable

class VehiclesListViewModel : ViewModel(), Serializable {
    val progressBarObserver = MutableLiveData<Boolean>()
    val removeBeaconOrBluetoothObserver = MutableLiveData<Boolean>()
    val vehiclesData = MutableLiveData<List<Vehicle>>()
    var vehiclesList: List<Vehicle> = listOf()
    var syncStatus: VehicleSyncStatus = VehicleSyncStatus.NO_ERROR

    fun fetchVehicles(
        context: Context,
        synchronizationType: SynchronizationType = SynchronizationType.DEFAULT
    ) {
        if (synchronizationType == SynchronizationType.DEFAULT) {
            progressBarObserver.postValue(true)
        }
        if (DriveKit.isConfigured()) {
            DriveKitVehicle.getVehiclesOrderByNameAsc(object : VehicleListQueryListener {
                override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                    progressBarObserver.postValue(false)
                    vehiclesList = VehicleUtils().fetchVehiclesOrderedByDisplayName(context)
                    vehiclesData.postValue(vehiclesList)
                }
            }, synchronizationType)
        } else {
            if (synchronizationType == SynchronizationType.DEFAULT) {
                progressBarObserver.postValue(false)
            }
            vehiclesData.postValue(listOf())
        }
    }

    fun hasLocalVehicles() = DriveKitVehicle.vehiclesQuery().noFilter().query().execute().isNotEmpty()

    fun getScreenTitle(context: Context) = if (vehiclesList.size > 1) {
        "dk_vehicle_my_vehicles"
    } else {
        "dk_vehicle_my_vehicle"
    }.let {
        DKResource.convertToString(context, it)
    }

    fun getTitle(context: Context, vehicle: Vehicle) = vehicle.buildFormattedName(context)

    fun getSubtitle(context: Context, vehicle: Vehicle) = vehicle.computeSubtitle(context)

    fun removeBeaconToVehicle(vehicle: Vehicle) {
        progressBarObserver.postValue(true)
        DriveKitVehicle.removeBeaconToVehicle(vehicle, object : VehicleRemoveBeaconQueryListener {
            override fun onResponse(status: VehicleBeaconRemoveStatus) {
                progressBarObserver.postValue(false)
                removeBeaconOrBluetoothObserver.postValue(status == VehicleBeaconRemoveStatus.SUCCESS)
            }
        })
    }

    fun removeBluetoothToVehicle(vehicle: Vehicle) {
        progressBarObserver.postValue(true)
        DriveKitVehicle.removeBluetoothToVehicle(
            vehicle,
            object : VehicleRemoveBluetoothQueryListener {
                override fun onResponse(status: VehicleRemoveBluetoothStatus) {
                    progressBarObserver.postValue(false)
                    removeBeaconOrBluetoothObserver.postValue(status == VehicleRemoveBluetoothStatus.SUCCESS)
                }
            })
    }

    fun buildDetectionModeSpinnerItems(context: Context): List<DetectionModeSpinnerItem> {
        val detectionModeSpinnerItems = mutableListOf<DetectionModeSpinnerItem>()
        for (detectionMode in DriveKitVehicleUI.detectionModes) {
            detectionModeSpinnerItems.add(
                DetectionModeSpinnerItem(
                    context,
                    DetectionModeType.getEnumByDetectionMode(detectionMode)
                )
            )
        }
        return detectionModeSpinnerItems
    }

    fun shouldDisplayButton() =
        DriveKitVehicleUI.canAddVehicle && (!maxVehiclesReached() || shouldReplaceVehicle())

    fun getVehicleButtonTextResId() =
        if (shouldReplaceVehicle()) "dk_vehicle_replace" else "dk_vehicle_add"

    fun shouldReplaceVehicle() =
        DriveKitVehicleUI.maxVehicles == 1 && DriveKitVehicleUI.canAddVehicle && vehiclesList.size == 1

    private fun maxVehiclesReached() = DriveKitVehicleUI.maxVehicles?.let {
        vehiclesList.size >= it
    } ?: false
}
