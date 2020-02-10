package com.drivequant.drivekit.driverachievement.ui.utils

import java.text.SimpleDateFormat
import java.util.*


class DateUtils {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    fun formatDate(date: Date) : String {
       return dateFormat.format(date)
    }
}