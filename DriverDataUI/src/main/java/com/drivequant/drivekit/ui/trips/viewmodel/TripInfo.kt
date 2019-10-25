package com.drivequant.drivekit.ui.trips.viewmodel

import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R

enum class TripInfo {
    NONE, ECO_DRIVING, SAFETY, COUNT;

    fun shouldDisplay(trip: Trip) : Boolean {
        return this != NONE && trip.tripAdvices.isNotEmpty()
    }

    fun imageResId() : Int? {
        return when (this){
            ECO_DRIVING -> R.drawable.dk_eco_advice
            SAFETY -> R.drawable.dk_safety_advice
            COUNT -> R.drawable.dk_trip_info_count
            else -> null
        }
    }

    fun text(trip: Trip) : String? {
        return when (this) {
            COUNT -> trip.tripAdvices.size.toString()
            else -> null
        }
    }
}