package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.Query
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometer
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometerHistory
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.odometer.*
import java.util.*

class OdometerHistoryDetailViewModel(val vehicleId: String, private val historyId: Int) : ViewModel() {

    private var vehicleOdometerHistory: VehicleOdometerHistory? = null

    init {
        vehicleOdometerHistory = DriveKitVehicle.odometerHistoriesQuery()
            .whereEqualTo("vehicleId", vehicleId)
            .and()
            .whereEqualTo("historyId", historyId)
            .queryOne()
            .executeOne()
    }

    fun canDeleteHistory(): Boolean {
        val histories =
            DriveKitVehicle.odometerHistoriesQuery().whereEqualTo("vehicleId", vehicleId).query()
                .execute()
        return histories.isNotEmpty() && histories.size > 1 && historyId > -1
    }

    fun canEditHistory() = historyId == getMostRecentHistory()?.historyId

    fun isAddMode() = historyId == -1

    private fun getMostRecentHistory() = DriveKitVehicle.odometerHistoriesQuery()
        .whereEqualTo("vehicleId", vehicleId)
        .orderBy("distance", Query.Direction.DESCENDING)
        .queryOne()
        .executeOne()

    fun getHistoryDistance(context: Context) = vehicleOdometerHistory?.let {
        DKDataFormatter.formatMeterDistanceInKm(context, it.distance * 1000).convertToString()
    } ?: "0"

    fun getHistoryUpdateDate() = if (isAddMode()) {
        Calendar.getInstance().time
    } else {
        vehicleOdometerHistory?.updateDate
    }?.formatDate(DKDatePattern.FULL_DATE)?.capitalizeFirstLetter() ?: ""


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
        DriveKitVehicle.deleteOdometerHistory(
            vehicleId,
            historyId,
            object : OdometerDeleteHistoryQueryListener {
                override fun onResponse(
                    status: OdometerDeleteHistoryStatus,
                    odometer: VehicleOdometer?,
                    histories: List<VehicleOdometerHistory>
                ) {
                    Log.e("TEST_SERVICE", "form delete $status")
                }
            })
    }

    @Suppress("UNCHECKED_CAST")
    class OdometerHistoryDetailViewModelFactory(
        private val vehicleId: String,
        private val historyId: Int
    ) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return OdometerHistoryDetailViewModel(vehicleId, historyId) as T
        }
    }
}