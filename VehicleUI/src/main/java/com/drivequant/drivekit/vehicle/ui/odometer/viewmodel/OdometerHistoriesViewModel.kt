package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKUnitSystem
import com.drivequant.drivekit.common.ui.utils.Meter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import java.util.*

internal class OdometerHistoriesViewModel(val vehicleId: String) : ViewModel() {

    fun getOdometerHistoriesList() =
        DriveKitVehicle.odometerHistoriesQuery().whereEqualTo("vehicleId", vehicleId).query()
            .execute().sortedByDescending {
                it.updateDate
            }.map {
                OdometerHistoryData(
                    it.historyId,
                    it.realDistance,
                    it.updateDate
                )
            }

    @Suppress("UNCHECKED_CAST")
    internal class OdometerHistoriesViewModelFactory(private val vehicleId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OdometerHistoriesViewModel(vehicleId) as T
        }
    }
}

internal data class OdometerHistoryData(
    val historyId: Int,
    private val realDistance: Double,
    private val date: Date?) {
    fun getRealDistance(context: Context) = DKDataFormatter.formatInKmOrMile(
        context,
        Meter(realDistance * 1000),
        minDistanceToRemoveFractions = 0.0,
        forcedUnitSystem = DKUnitSystem.METRIC
    ).convertToString()
    fun getUpdateDate() = date?.formatDate(DKDatePattern.FULL_DATE)
}
