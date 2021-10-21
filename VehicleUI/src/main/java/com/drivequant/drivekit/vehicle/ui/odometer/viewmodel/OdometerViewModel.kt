package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometer
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometerHistory
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.odometer.OdometerSyncQueryListener
import com.drivequant.drivekit.vehicle.odometer.OdometerSyncStatus
import java.util.*

class OdometerViewModel : ViewModel() {

    var filterItems: MutableList<FilterItem> = mutableListOf()
    val filterData: MutableLiveData<List<FilterItem>> = MutableLiveData()
    val odometerData: MutableLiveData<Boolean> = MutableLiveData()
    var vehicleOdometer: VehicleOdometer? = null
    var currentVehicleId: String? = null

    fun updateVehicleMileageData(vehicleId: String, synchronizationType: SynchronizationType) {
        currentVehicleId = vehicleId
        if (DriveKit.isConfigured()) {
            DriveKitVehicle.getOdometer(vehicleId, object : OdometerSyncQueryListener {
                override fun onResponse(
                    status: OdometerSyncStatus,
                    odometer: VehicleOdometer?,
                    histories: List<VehicleOdometerHistory>
                ) {
                    if (status != OdometerSyncStatus.VEHICLE_NOT_FOUND) {
                        vehicleOdometer = odometer
                        odometerData.postValue(true)
                    } else {
                        odometerData.postValue(false)
                    }
                }
            }, synchronizationType)
        } else {
            odometerData.postValue(false)
        }
    }

    fun getFilterItems(context: Context) {
        filterItems.clear()
        DriveKitNavigationController.vehicleUIEntryPoint?.getVehiclesFilterItems(context)?.let {
            filterItems.addAll(it)
        }
        filterData.postValue(filterItems)
    }

    //TODO ${DKResource.convertToString(context)}
    fun getDate(context: Context) = "Last update ${vehicleOdometer?.updateDate?.formatDate(DKDatePattern.STANDARD_DATE)}"

    fun getDistance(context: Context) = vehicleOdometer?.distance?.let { DKDataFormatter.formatMeterDistance(context,it * 1000).convertToString() }

    fun getTotalDistance(context: Context) {

    }

    fun getTotalDistanceLastUpdate() {

    }

    fun getAnalyzedDistance() {

    }

    fun getAnalyzedDistanceDescription() {

    }

    fun getEstimatedDistance() {

    }

    fun getEstimatedDistanceDescription() {

    }

    fun getOdometerHistory(historyId: String) {

    }
}