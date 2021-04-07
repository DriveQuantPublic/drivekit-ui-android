package com.drivequant.drivekit.common.ui.component.tripslist

import android.content.Context
import android.graphics.drawable.Drawable
import java.io.Serializable
import java.util.*


interface DKTripsList : Serializable {
    fun onTripClickListener(itinId: String)
    fun getTripData(): TripData
    fun getTripsList(): List<DKTripsByDate>
}

interface DKTripListItem : Serializable {
    fun getItinId(): String
    fun getDuration(): Double?
    fun getDistance(): Double?
    fun getEndDate(): Date
    fun getDepartureCity(): String
    fun getArrivalCity(): String
    fun isScored(tripData: TripData): Boolean
    fun getScore(tripData: TripData): Double?
    fun getTransportationModeResourceId(context:Context): Drawable?
    fun infoText(): String?
    fun infoImageResource(): Int?
    fun infoClickAction(context: Context)
    fun hasInfoActionConfigured(): Boolean
    fun isDisplayable(): Boolean?
}