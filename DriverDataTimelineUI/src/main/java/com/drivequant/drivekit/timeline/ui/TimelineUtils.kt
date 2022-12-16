package com.drivequant.drivekit.timeline.ui

import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun String.toTimelineDate(): Date? = TimelineUtils.getBackendDateFormat().parse(this)
fun Date.toTimelineString(): String = TimelineUtils.getBackendDateFormat().format(this)

fun DKTimelinePeriod.getTitleResId() = when(this) {
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