package com.drivequant.drivekit.ui.drivingconditions.component.summary

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter

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

    fun formatDistanceKm() = DKDataFormatter.formatDistanceValue(this.distance, 10.0)
}
