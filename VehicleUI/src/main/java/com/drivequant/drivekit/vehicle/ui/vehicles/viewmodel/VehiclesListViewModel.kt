package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import com.drivequant.drivekit.vehicle.ui.R
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
                    vehiclesList = VehicleUtils.fetchVehiclesOrderedByDisplayName(context)
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

    fun hasLocalVehicles() = DriveKitVehicle.vehiclesQuery().noFilter().countQuery().execute() > 0

    fun getScreenTitle(context: Context) =
        if (vehiclesList.size > 1) {
            R.string.dk_vehicle_my_vehicles
        } else {
            R.string.dk_vehicle_my_vehicle
        }.let {
            context.getString(it)
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

    fun shouldDisplayAddReplaceButton() =
        DriveKitVehicleUI.canAddVehicle && (!maxVehiclesReached() || shouldReplaceVehicle())

    @StringRes
    fun getAddReplaceButtonTextResId(): Int =
        if (shouldReplaceVehicle()) R.string.dk_vehicle_replace_button else R.string.dk_vehicle_add

    fun shouldReplaceVehicle() =
        DriveKitVehicleUI.vehicleActions.contains(VehicleAction.REPLACE) && DriveKitVehicleUI.maxVehicles == 1 && vehiclesList.size == 1

    private fun maxVehiclesReached() = DriveKitVehicleUI.maxVehicles?.let {
        vehiclesList.size >= it
    } ?: false
}
