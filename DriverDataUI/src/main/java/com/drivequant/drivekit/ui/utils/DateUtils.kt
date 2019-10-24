package com.drivequant.drivekit.ui.utils

import com.drivequant.drivekit.databaseutils.entity.Trip
import java.util.*

class DateUtils {
    fun isSameDay(trip: Trip, date: Date) : Boolean {
        val tripCal: Calendar = Calendar.getInstance()
        tripCal.set(trip.endDate.year, trip.endDate.month, trip.endDate.day)

        val currentDateCal: Calendar = Calendar.getInstance()
        currentDateCal.set(date.year, date.month, date.day)

        return (tripCal.get(Calendar.DAY_OF_YEAR) == currentDateCal.get(Calendar.DAY_OF_YEAR)
                && tripCal.get(Calendar.YEAR) == currentDateCal.get(Calendar.YEAR) )
    }
}