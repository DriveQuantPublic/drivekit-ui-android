package com.drivequant.drivekit.timeline.ui

import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal fun String.toTimelineDate(): Date? = TimelineUtils.getBackendDateFormat().parse(this)
internal fun Date.toTimelineString(): String = TimelineUtils.getBackendDateFormat().format(this)

internal fun Timeline.totalDistanceForAllContexts(selectedScore: DKTimelineScoreType, selectedIndex: Int) : Double {
    var totalDistanceForAllContexts = 0.0
    if (this.hasValidTripScored(selectedScore, selectedIndex)) {
        totalDistanceForAllContexts = this.allContext.distance[selectedIndex]
    }
    return totalDistanceForAllContexts
}

private fun Timeline.hasValidTripScored(selectedScore: DKTimelineScoreType, selectedIndex: Int) =
    selectedScore == DKTimelineScoreType.DISTRACTION
            || selectedScore == DKTimelineScoreType.SPEEDING
            || this.allContext.numberTripScored[selectedIndex] > 0

internal fun DKTimelinePeriod.getTitleResId() = when(this) {
    DKTimelinePeriod.WEEK -> "dk_timeline_per_week"
    DKTimelinePeriod.MONTH -> "dk_timeline_per_month"
}

private object TimelineUtils {
    fun getBackendDateFormat(): DateFormat {
        val backendDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        backendDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return backendDateFormat
    }
}