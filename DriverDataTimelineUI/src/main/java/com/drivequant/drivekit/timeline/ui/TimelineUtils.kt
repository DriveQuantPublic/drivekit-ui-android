package com.drivequant.drivekit.timeline.ui

import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal fun String.toTimelineDate(): Date? = TimelineUtils.getBackendDateFormat().parse(this)
internal fun Date.toTimelineString(): String = TimelineUtils.getBackendDateFormat().format(this)

internal fun Timeline.totalDistanceForAllContexts(selectedScore: DKScoreType, selectedIndex: Int) : Double {
    var totalDistanceForAllContexts = 0.0
    if (this.hasValidTripScored(selectedScore, selectedIndex)) {
        totalDistanceForAllContexts = this.allContext.distance[selectedIndex, 0.0]
    }
    return totalDistanceForAllContexts
}

private fun Timeline.hasValidTripScored(selectedScore: DKScoreType, selectedIndex: Int) =
    selectedScore == DKScoreType.DISTRACTION
            || selectedScore == DKScoreType.SPEEDING
            || this.allContext.numberTripScored[selectedIndex, 0] > 0

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


internal fun <E> List<E>.getSafe(index: Int): E? {
    return if (index < 0 || index >= this.size) {
        null
    } else {
        this[index]
    }
}

internal operator fun <E> List<E>.get(index: Int, default: E): E = getSafe(index) ?: default

internal fun <E> List<E>.addValueIfNotEmpty(index: Int, list: MutableList<E>) {
    if (this.isNotEmpty()) {
        list.add(this[index])
    }
}
