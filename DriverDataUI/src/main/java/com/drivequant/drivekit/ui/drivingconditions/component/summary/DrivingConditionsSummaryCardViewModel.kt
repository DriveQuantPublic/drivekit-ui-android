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

    fun getTripsCount() = driverTimeline?.allContext?.let {
        NumberFormat.getNumberInstance().format(it[selectedDateIndex].numberTripTotal)
    }

    fun getDistanceKm(): String? {
        val distanceKm = driverTimeline?.allContext?.let {
            it[selectedDateIndex].distance
        }
        distanceKm?.let {
            return DKDataFormatter.formatDistanceValue(distanceKm, 100.0)
        }
        return null
    }
}