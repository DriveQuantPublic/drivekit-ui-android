package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometer
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometerHistory
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.odometer.OdometerSyncQueryListener
import com.drivequant.drivekit.vehicle.odometer.OdometerSyncStatus

class OdometerVehicleListViewModel : ViewModel() {

    var filterItems: MutableList<FilterItem> = mutableListOf()
    val filterData: MutableLiveData<List<FilterItem>> = MutableLiveData()
    val vehicleOdometerData: MutableLiveData<Boolean> = MutableLiveData()
    var selection: MutableLiveData<String> = MutableLiveData()

    fun getOdometer(vehicleId: String, synchronizationType: SynchronizationType) {
        if (DriveKit.isConfigured()) {
            DriveKitVehicle.getOdometer(vehicleId, object : OdometerSyncQueryListener {
                override fun onResponse(
                    status: OdometerSyncStatus,
                    odometer: VehicleOdometer?,
                    histories: List<VehicleOdometerHistory>
                ) {
                    if (status != OdometerSyncStatus.VEHICLE_NOT_FOUND) {
                        vehicleOdometerData.postValue(true)
                    } else {
                        vehicleOdometerData.postValue(false)
                    }
                }
            }, synchronizationType)
        } else {
            vehicleOdometerData.postValue(false)
        }
    }

    fun getVehicleListItems(context: Context) {
        filterItems.clear()
        DriveKitNavigationController.vehicleUIEntryPoint?.getVehiclesFilterItems(context)?.let {
            filterItems.addAll(it)
        }
        selection.value = filterItems.first().getItemId() as String
        filterData.postValue(filterItems)
    }
}