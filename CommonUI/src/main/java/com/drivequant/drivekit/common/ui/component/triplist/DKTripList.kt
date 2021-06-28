package com.drivequant.drivekit.common.ui.component.triplist

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.DKHeader
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import java.util.*

interface DKTripList {
    fun onTripClickListener(itinId: String)
    fun getTripData(): TripData
    fun getTripsList(): List<DKTripListItem>
    fun getCustomHeader(): DKHeader?
    fun getHeaderDay(): HeaderDay
    fun getDayTripDescendingOrder(): Boolean
    fun canSwipeToRefresh(): Boolean
    fun onSwipeToRefresh()
}

interface DKTripListItem {
    fun getChildObject(): Any
    fun getItinId(): String
    fun getDuration(): Double?
    fun getDistance(): Double?
    fun getStartDate(): Date?
    fun getEndDate(): Date
    fun getDepartureCity(): String
    fun getArrivalCity(): String
    fun isScored(tripData: TripData): Boolean
    fun getScore(tripData: TripData): Double?
    fun getTransportationModeResource(context: Context): Drawable?
    fun isAlternative(): Boolean
    fun infoText(context: Context): Spannable?
    fun infoImageResource(): Int?
    fun infoClickAction(context: Context)
    fun hasInfoActionConfigured(): Boolean
    fun isInfoDisplayable(): Boolean
}