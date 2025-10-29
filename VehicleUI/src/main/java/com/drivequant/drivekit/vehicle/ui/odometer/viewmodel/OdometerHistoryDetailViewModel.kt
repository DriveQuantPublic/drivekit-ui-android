package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKUnitSystem
import com.drivequant.drivekit.common.ui.utils.Meter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.Query
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometer
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometerHistory
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.odometer.OdometerAddHistoryQueryListener
import com.drivequant.drivekit.vehicle.odometer.OdometerAddHistoryStatus
import com.drivequant.drivekit.vehicle.odometer.OdometerDeleteHistoryQueryListener
import com.drivequant.drivekit.vehicle.odometer.OdometerDeleteHistoryStatus
import com.drivequant.drivekit.vehicle.odometer.OdometerUpdateHistoryQueryListener
import com.drivequant.drivekit.vehicle.odometer.OdometerUpdateHistoryStatus
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import java.util.Calendar

internal class OdometerHistoryDetailViewModel(val vehicleId: String, private val historyId: Int) :
    ViewModel() {

    private var vehicleOdometerHistory: VehicleOdometerHistory? = null
    var odometerActionObserver: MutableLiveData<Pair<Int, Boolean>> = MutableLiveData()
    var mileageDistance = 0.0

    init {
        vehicleOdometerHistory = DriveKitVehicle.odometerHistoriesQuery()
            .whereEqualTo("vehicleId", vehicleId)
            .and()
            .whereEqualTo("historyId", historyId)
            .queryOne()
            .executeOne()
    }

    fun canDeleteHistory() = historyId > -1 && DriveKitVehicle.odometerHistoriesQuery().whereEqualTo("vehicleId", vehicleId).countQuery()
            .execute().let { it > 1 }

    fun canEditHistory() = historyId == getMostRecentHistory()?.historyId

    fun canAddHistory() = historyId == -1

    fun canEditOrAddHistory() = canEditHistory() || canAddHistory()

    private fun getMostRecentHistory() = DriveKitVehicle.odometerHistoriesQuery()
        .whereEqualTo("vehicleId", vehicleId)
        .orderBy("updateDate", Query.Direction.DESCENDING)
        .queryOne()
        .executeOne()

    fun getHistoryDistance(context: Context) = vehicleOdometerHistory?.let {
        mileageDistance = it.distance
        DKDataFormatter.formatInKmOrMile(
            context,
            Meter(it.distance * 1000),
            minDistanceToRemoveFractions = 0.0,
            forcedUnitSystem = DKUnitSystem.METRIC
        ).convertToString()
    } ?: context.getString(R.string.dk_vehicle_odometer_mileage_kilometer)

    fun getFormattedMileageDistance(context: Context, unit: Boolean = true) =
        DKDataFormatter.formatInKmOrMile(
            context,
            Meter(mileageDistance * 1000),
            unit,
            minDistanceToRemoveFractions = 0.0,
            forcedUnitSystem = DKUnitSystem.METRIC
        ).convertToString()

    fun getHistoryUpdateDate() = if (canAddHistory()) {
        Calendar.getInstance().time
    } else {
        vehicleOdometerHistory?.updateDate
    }?.formatDate(DKDatePattern.FULL_DATE)?.capitalizeFirstLetter() ?: ""

    fun showMileageDistanceErrorMessage() = when {
        mileageDistance >= 1000000.0 || mileageDistance < 0.0 -> true
        else -> false
    }

    fun getVehicleFormattedName(context: Context) =
        DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()
            ?.let {
                VehicleUtils.buildFormattedName(context, it)
            } ?: ""

    fun addOdometerHistory() {
        DriveKitVehicle.addOdometerHistory(
            vehicleId,
            mileageDistance,
            object : OdometerAddHistoryQueryListener {
                override fun onResponse(
                    status: OdometerAddHistoryStatus,
                    odometer: VehicleOdometer?,
                    histories: List<VehicleOdometerHistory>) {
                    when (status) {
                        OdometerAddHistoryStatus.SUCCESS -> Pair(R.string.dk_vehicle_odometer_history_add_success, true)
                        OdometerAddHistoryStatus.FAILED ->  Pair(com.drivequant.drivekit.common.ui.R.string.dk_common_error_message, false)
                        OdometerAddHistoryStatus.VEHICLE_NOT_FOUND ->  Pair(R.string.dk_vehicle_not_found, false)
                        OdometerAddHistoryStatus.BAD_DISTANCE ->   Pair(R.string.dk_vehicle_odometer_bad_distance, false)
                    }.let {
                        odometerActionObserver.postValue(it)
                    }
                }
            })
    }

    fun updateOdometerHistory() {
        DriveKitVehicle.updateOdometerHistory(
            vehicleId,
            historyId,
            mileageDistance,
            object : OdometerUpdateHistoryQueryListener {
                override fun onResponse(
                    status: OdometerUpdateHistoryStatus,
                    odometer: VehicleOdometer?,
                    histories: List<VehicleOdometerHistory>) {
                    when (status) {
                        OdometerUpdateHistoryStatus.SUCCESS -> Pair(R.string.dk_vehicle_odometer_history_update_success, true)
                        OdometerUpdateHistoryStatus.FAILED -> Pair(com.drivequant.drivekit.common.ui.R.string.dk_common_error_message, false)
                        OdometerUpdateHistoryStatus.HISTORY_NOT_FOUND -> Pair(R.string.dk_vehicle_odometer_history_not_found, false)
                        OdometerUpdateHistoryStatus.VEHICLE_NOT_FOUND -> Pair(R.string.dk_vehicle_not_found, false)
                        OdometerUpdateHistoryStatus.BAD_DISTANCE -> Pair(R.string.dk_vehicle_odometer_bad_distance, false)
                    }.let {
                        odometerActionObserver.postValue(it)
                    }
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
                    histories: List<VehicleOdometerHistory>) {
                    when (status) {
                        OdometerDeleteHistoryStatus.SUCCESS -> Pair(R.string.dk_vehicle_odometer_history_delete_success, true)
                        OdometerDeleteHistoryStatus.FAILED -> Pair(com.drivequant.drivekit.common.ui.R.string.dk_common_error_message, false)
                        OdometerDeleteHistoryStatus.VEHICLE_NOT_FOUND -> Pair(R.string.dk_vehicle_not_found, false)
                        OdometerDeleteHistoryStatus.HISTORY_NOT_FOUND -> Pair(R.string.dk_vehicle_odometer_history_not_found, false)
                        OdometerDeleteHistoryStatus.LAST_ODOMETER_ERROR -> Pair(R.string.dk_vehicle_odometer_last_history_error, false)
                    }.let {
                        odometerActionObserver.postValue(it)
                    }
                }
            })
    }

    @Suppress("UNCHECKED_CAST")
    internal class OdometerHistoryDetailViewModelFactory(
        private val vehicleId: String,
        private val historyId: Int) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OdometerHistoryDetailViewModel(vehicleId, historyId) as T
        }
    }
}
