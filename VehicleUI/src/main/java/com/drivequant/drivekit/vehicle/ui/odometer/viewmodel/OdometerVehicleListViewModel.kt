package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometer
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometerHistory
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.odometer.OdometerSyncQueryListener
import com.drivequant.drivekit.vehicle.odometer.OdometerSyncStatus

internal class OdometerVehicleListViewModel(val vehicleId: String?) : ViewModel() {

    var filterItems: MutableList<FilterItem> = mutableListOf()
    val filterData: MutableLiveData<Int> = MutableLiveData()
    val vehicleOdometerData: MutableLiveData<Boolean> = MutableLiveData()
    var selection: MutableLiveData<String?> = MutableLiveData()

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
        filterItems.apply {
            clear()
            DriveKitNavigationController.vehicleUIEntryPoint?.getVehiclesFilterItems(context)?.let {
                addAll(it)
            }
        }
        selection.value = vehicleId ?: if (filterItems.isNotEmpty()) filterItems.first().getItemId() as String else null
        val position = vehicleId?.let {
            var index = 0
            filterItems.forEachIndexed { i, filterItem ->
                if (filterItem.getItemId() == it) {
                  index =  i
                }
            }
            index
        } ?:  0
        filterData.postValue(position)
    }

    @Suppress("UNCHECKED_CAST")
    internal class OdometerVehicleListViewModelFactory(
        private val vehicleId: String?) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return OdometerVehicleListViewModel(vehicleId) as T
        }
    }
}