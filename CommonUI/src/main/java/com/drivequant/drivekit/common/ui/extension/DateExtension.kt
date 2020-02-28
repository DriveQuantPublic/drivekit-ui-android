package com.drivequant.drivekit.common.ui.extension

import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import java.text.SimpleDateFormat
import java.util.*

fun Date.formatDate(pattern: DKDatePattern) : String{
    val dateFormat = SimpleDateFormat(pattern.getPattern(), Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.isSameDay(date: Date) : Boolean {
    val tripCal: Calendar = Calendar.getInstance()
    tripCal.set(this.year, this.month, this.day)

    val currentDateCal: Calendar = Calendar.getInstance()
    currentDateCal.set(date.year, date.month, date.day)

    return (tripCal.get(Calendar.DAY_OF_YEAR) == currentDateCal.get(Calendar.DAY_OF_YEAR)
            && tripCal.get(Calendar.YEAR) == currentDateCal.get(Calendar.YEAR) )
}