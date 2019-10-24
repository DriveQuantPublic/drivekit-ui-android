package com.drivequant.drivekit.ui.extension

import java.text.SimpleDateFormat
import java.util.*

fun Date.formatHeaderDay() : String {
    val dateFormatDate = SimpleDateFormat("EEEE d MMMM", Locale.getDefault())
    return dateFormatDate.format(this)
}

fun Date.formatHour() : String {
    val dateFormatDate = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormatDate.format(this)
}