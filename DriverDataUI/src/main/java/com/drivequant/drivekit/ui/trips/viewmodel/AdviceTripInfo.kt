package com.drivequant.drivekit.ui.trips.viewmodel

import android.app.Activity
import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity

class AdviceTripInfo : DKTripInfo {

    override fun isDisplayable(trip: Trip): Boolean = !trip.tripAdvices.isNullOrEmpty()

    override fun getImageResource(trip: Trip): Int? {
        val count = trip.tripAdvices.size
        if (count > 1) {
            return R.drawable.dk_trip_info_count
        } else if (count == 1) {
            val theme = trip.tripAdvices.first().theme
            if (theme == "SAFETY") {
                return R.drawable.dk_safety_advice
            } else if (theme == "ECODRIVING") {
                return R.drawable.dk_eco_advice
            }
        }
        return null
    }

    override fun onClickAction(context: Context, trip: Trip) {
        TripDetailActivity.launchActivity(context as Activity,
            trip.itinId,
            openAdvice = true)
    }

    override fun hasActionConfigured(trip: Trip): Boolean = true

    override fun text(trip: Trip): String? {
        return if (trip.tripAdvices.size > 1) {
            trip.tripAdvices.size.toString()
        } else {
            null
        }
    }
}