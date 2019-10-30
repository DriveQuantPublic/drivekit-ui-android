package com.drivequant.drivekit.ui.trips.viewmodel

import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R

enum class TripInfo {
    NONE, ECO_DRIVING, SAFETY, COUNT;
    
    fun imageResId(trip: Trip): Int? {
        val count = trip.tripAdvices.size
        if (count > 1){
            return R.drawable.dk_trip_info_count
        } else if (count == 1){
            val theme = trip.tripAdvices.first().theme
            if (theme == "SAFETY"){
                return R.drawable.dk_safety_advice
            } else if (theme == "ECODRIVING"){
                return R.drawable.dk_eco_advice
            }
        }
        return null
    }

    fun text(trip: Trip): String? {
        return if (trip.tripAdvices.size > 1){
            trip.tripAdvices.size.toString()
        } else {
            null
        }
    }
}