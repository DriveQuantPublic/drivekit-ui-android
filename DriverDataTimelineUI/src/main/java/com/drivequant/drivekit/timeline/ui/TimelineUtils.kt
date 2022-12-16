package com.drivequant.drivekit.timeline.ui

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun String.toTimelineDate(): Date? = TimelineUtils.getBackendDateFormat().parse(this)
fun Date.toTimelineString(): String = TimelineUtils.getBackendDateFormat().format(this)

private object TimelineUtils {
    fun getBackendDateFormat(): DateFormat {
        val backendDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        backendDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return backendDateFormat
    }
}