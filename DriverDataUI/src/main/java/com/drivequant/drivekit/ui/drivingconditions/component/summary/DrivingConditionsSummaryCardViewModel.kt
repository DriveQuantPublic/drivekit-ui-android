package com.drivequant.drivekit.ui.drivingconditions.component.summary

import android.content.Context
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.Meter

internal class DrivingConditionsSummaryCardViewModel : ViewModel() {

    var onViewModelUpdated: (() -> Unit)? = null
    var tripCount: Int = 0
        private set
    var distance: Double = 0.0
        private set

    fun configure(tripCount: Int = 0, distance: Double = 0.0) {
        this.tripCount = tripCount
        this.distance = distance
        this.onViewModelUpdated?.invoke()
    }

    fun hasData() = this.tripCount > 0 && this.distance > 0.0

    fun formatTripsCount(): String = DKDataFormatter.formatNumber(this.tripCount)

    fun formatDistanceKmOld() = DKDataFormatter.formatDistanceValue(this.distance, 10.0)
    fun formatDistanceKm(context: Context) = DKDataFormatter.formatInKmOrMile(context, Meter(this.distance * 1000), unit = false, minDistanceToRemoveFractions = 10.0)
}
