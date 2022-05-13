package com.drivekit.demoapp.splashscreen.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.drivekit.demoapp.manager.*
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus

@SuppressLint("CustomSplashScreen")
class SplashScreenViewModel {

    val syncFinished: MutableLiveData<Any> = MutableLiveData()
    val shouldShowVehicles: MutableLiveData<Boolean> = MutableLiveData()

    fun syncDriveKitModules() {
        SyncModuleManager.syncModules(
            mutableListOf(
                DKModule.TRIPS,
                DKModule.CHALLENGE,
                DKModule.USER_INFO,
                DKModule.VEHICLE), listener = object : ModulesSyncListener {
                override fun onModulesSyncResult(results: MutableList<SyncStatus>) {
                    syncFinished.postValue(Any())
                }
            }
        )
    }

    fun shouldShowVehicles() =
        DriveKitVehicle.getVehiclesOrderByNameAsc(object : VehicleListQueryListener {
            override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                shouldShowVehicles.postValue(vehicles.isEmpty())
            }
        }, SynchronizationType.CACHE)
}