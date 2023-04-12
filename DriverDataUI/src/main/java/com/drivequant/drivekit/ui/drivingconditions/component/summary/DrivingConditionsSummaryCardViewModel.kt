package com.drivequant.drivekit.ui.drivingconditions.component.summary

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import java.text.NumberFormat

internal class DrivingConditionsSummaryCardViewModel : ViewModel() {

    var onViewModelUpdated: (() -> Unit)? = null
    private var selectedPeriod: DKPeriod = DKPeriod.WEEK
    private var driverTimeline: DKDriverTimeline? = null
    private var selectedDateIndex: Int = 0

    fun configure(
        period: DKPeriod,
        driverTimeline: DKDriverTimeline?,
        selectedDateIndex: Int
    ) {
        this.selectedPeriod = period
        this.driverTimeline = driverTimeline
        this.selectedDateIndex = selectedDateIndex
        this.onViewModelUpdated?.invoke()
    }

    fun hasData(): Boolean {
        return driverTimeline?.allContext?.get(selectedDateIndex)?.numberTripTotal?.let {
            it > 0
        } ?: false
    }
    fun getTripsCount() = driverTimeline?.allContext?.get(selectedDateIndex)?.numberTripTotal ?: 0

    fun formatTripsCount(): String = NumberFormat.getNumberInstance().format(getTripsCount())

    fun getDistanceKm(): String? {
        val distanceKm = driverTimeline?.allContext?.get(selectedDateIndex)?.distance ?: 0.0
        return DKDataFormatter.formatDistanceValue(distanceKm, 100.0)
    }
}