package com.drivequant.drivekit.ui.trips.viewmodel

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Trip

interface DKTripInfo {
    fun getImageResource(trip: Trip): Int?
    fun text(trip: Trip): String?
    fun isDisplayable(trip: Trip): Boolean
    fun onClickAction(context: Context, trip: Trip)
    fun hasActionConfigured(trip: Trip): Boolean
}