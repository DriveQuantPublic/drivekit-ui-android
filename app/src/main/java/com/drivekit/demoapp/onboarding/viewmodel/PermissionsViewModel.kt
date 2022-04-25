package com.drivekit.demoapp.onboarding.viewmodel

import androidx.lifecycle.MutableLiveData
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus

internal class PermissionsViewModel {

    var shouldDisplayVehicle: MutableLiveData<Boolean> = MutableLiveData()

    fun shouldDisplayVehicle() {
        DriveKitVehicle.getVehiclesOrderByNameAsc(object : VehicleListQueryListener {
            override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                shouldDisplayVehicle.postValue(vehicles.isEmpty())
            }
        }, SynchronizationType.CACHE)
    }
}