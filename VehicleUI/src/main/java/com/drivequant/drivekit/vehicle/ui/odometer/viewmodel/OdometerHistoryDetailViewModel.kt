package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometer
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometerHistory
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.odometer.*

class OdometerHistoryDetailViewModel(val vehicleId: String, private val historyId: Int) : ViewModel() {

    private var vehicleOdometerHistory: VehicleOdometerHistory? = null

    init {
        vehicleOdometerHistory =
            DriveKitVehicle.odometerHistoriesQuery().whereEqualTo("historyId", historyId).queryOne()
                .executeOne()
    }

    fun getVehicleOdometerHistory() = vehicleOdometerHistory

    fun addOdometerHistory(distance: Double) {
        DriveKitVehicle.addOdometerHistory(
            vehicleId,
            distance,
            object : OdometerAddHistoryQueryListener {
                override fun onResponse(
                    status: OdometerAddHistoryStatus,
                    odometer: VehicleOdometer?,
                    histories: List<VehicleOdometerHistory>
                ) {
                    Log.e("TEST_SERVICE", "from add $status")
                }
            })
    }

    fun updateOdometerHistory(distance: Double) {
        DriveKitVehicle.updateOdometerHistory(
            vehicleId,
            historyId,
            distance,
            object : OdometerUpdateHistoryQueryListener {
                override fun onResponse(
                    status: OdometerUpdateHistoryStatus,
                    odometer: VehicleOdometer?,
                    histories: List<VehicleOdometerHistory>
                ) {
                    Log.e("TEST_SERVICE", "from update : $status")
                }
            })
    }

    fun deleteOdometerHistory() {
        DriveKitVehicle.deleteOdometerHistory(vehicleId, historyId, object : OdometerDeleteHistoryQueryListener{
            override fun onResponse(
                status: OdometerDeleteHistoryStatus,
                odometer: VehicleOdometer?,
                histories: List<VehicleOdometerHistory>
            ) {
                Log.e("TEST_SERVICE", "form delete $status")
            }
        })
    }
}