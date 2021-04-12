package com.drivequant.drivekit.common.ui.component.tripslist

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.component.tripslist.viewModel.DKHeader
import com.drivequant.drivekit.common.ui.component.tripslist.viewModel.HeaderDay
import java.util.*


interface DKTripsList {
    fun onTripClickListener(itinId: String)
    fun getTripData(): TripData
    fun getTripsList(): List<DKTripListItem>
    fun getCustomHeader(): DKHeader?
    fun getHeaderDay() : HeaderDay
    fun getDayTripDescendingOrder() : Boolean
    fun canSwipeToRefresh(): Boolean
    fun onSwipeToRefresh()
}

interface DKTripListItem {
    fun getItinId(): String
    fun getDuration(): Double?
    fun getDistance(): Double?
    fun getEndDate(): Date
    fun getStartDate(): Date?
    fun getDepartureCity(): String
    fun getArrivalCity(): String
    fun isScored(tripData: TripData): Boolean
    fun getScore(tripData: TripData): Double?
    fun getTransportationModeResourceId(context:Context): Drawable?
    fun isAlternative(): Boolean
    fun infoText(): String?
    fun infoImageResource(): Int?
    fun infoClickAction(context: Context)
    fun hasInfoActionConfigured(): Boolean
    fun isDisplayable(): Boolean?
}