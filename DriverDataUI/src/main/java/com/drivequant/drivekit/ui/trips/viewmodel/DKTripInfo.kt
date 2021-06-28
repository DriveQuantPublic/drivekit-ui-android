package com.drivequant.drivekit.ui.trips.viewmodel

import android.content.Context
import android.text.Spannable
import com.drivequant.drivekit.databaseutils.entity.Trip

interface DKTripInfo {
    fun infoText(context: Context, trip: Trip): Spannable?
    fun infoImageResource(trip: Trip): Int?
    fun infoClickAction(context: Context, trip: Trip)
    fun hasInfoActionConfigured(trip: Trip): Boolean
    fun isInfoDisplayable(trip: Trip): Boolean
}