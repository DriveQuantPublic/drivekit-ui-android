package com.drivequant.drivekit.common.ui.component.triplist

import android.content.Context
import java.util.*


interface DKTripsList {
    fun onTripClickListener(itinId: String)
    fun getTripData(): TripData
    fun getTripsList(): List<DKTripListItem>
}

interface DKTripListItem {
    fun getItinId():String
    fun getDuration():Double
    fun getDistance():Double?
    fun getEndDate():Date
    fun getDepartureCity():String
    fun getArrivalCity():String
    fun isScored(tripData:TripData):Boolean
    fun getScore(tripData: TripData) :Boolean?
    fun getTransportationModeResourceId(): Int?
    fun infoText():String?
    fun infoImageResource(): Int?
    fun infoClickAction(context: Context)
    fun hasInfoActionConfigured():Boolean
    fun isDisplayable():String?
}