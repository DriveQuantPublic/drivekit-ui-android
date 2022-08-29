@file:JvmName("DKDateUtils")
package com.drivequant.drivekit.common.ui.extension

import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Date.formatDate(pattern: DKDatePattern): String {
    val dateFormat = SimpleDateFormat(pattern.getPattern(), Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.formatDateWithPattern(sdf : SimpleDateFormat): String = sdf.format(this)

fun Date.isSameDay(date: Date): Boolean {
    val tripCal: Calendar = Calendar.getInstance()
    tripCal.time = this

    val currentDateCal: Calendar = Calendar.getInstance()
    currentDateCal.time = date

    return (tripCal.get(Calendar.DAY_OF_YEAR) == currentDateCal.get(Calendar.DAY_OF_YEAR)
            && tripCal.get(Calendar.YEAR) == currentDateCal.get(Calendar.YEAR))
}

fun Date.getDaysDiff() =
    TimeUnit.DAYS.convert(Date().time - this.time, TimeUnit.MILLISECONDS).toInt()
