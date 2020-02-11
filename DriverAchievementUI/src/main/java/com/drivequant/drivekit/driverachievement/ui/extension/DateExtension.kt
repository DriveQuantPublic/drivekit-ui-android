package com.drivequant.drivekit.driverachievement.ui.extension

import java.text.SimpleDateFormat
import java.util.*

fun Date.formatStreaksDate(): String {
    val dateFormatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormatDate.format(this)
}