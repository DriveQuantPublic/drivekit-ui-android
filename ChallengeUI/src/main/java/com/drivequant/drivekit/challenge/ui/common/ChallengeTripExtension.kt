package com.drivequant.drivekit.challenge.ui.common

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.databaseutils.entity.Trip
import java.util.*
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.DriveKitUI

internal fun Trip.toDKTripItem() = object: DKTripListItem {
    val trip = this@toDKTripItem
    override fun getChildObject() = trip
    override fun getItinId(): String = trip.itinId
    override fun getDuration(): Double? = trip.tripStatistics?.duration
    override fun getDistance(): Double? = trip.tripStatistics?.distance
    override fun getStartDate(): Date? = trip.startDate
    override fun getEndDate(): Date = trip.endDate
    override fun getDepartureCity(): String = trip.departureCity
    override fun getDepartureAddress(): String = trip.departureAddress
    override fun getArrivalCity(): String = trip.arrivalCity
    override fun getArrivalAddress(): String = trip.arrivalAddress
    override fun isScored(tripData: TripData): Boolean =
        when (tripData) {
            TripData.SAFETY, TripData.ECO_DRIVING -> !trip.unscored
            TripData.DISTRACTION -> !trip.unscored && trip.driverDistraction != null
            TripData.SPEEDING -> !trip.unscored && trip.speedingStatistics != null
            TripData.DISTANCE, TripData.DURATION -> true
        }

    override fun getScore(tripData: TripData): Double? =
        when (tripData) {
            TripData.SAFETY -> trip.safety?.safetyScore
            TripData.ECO_DRIVING -> trip.ecoDriving?.score
            TripData.DISTRACTION -> trip.driverDistraction?.score
            TripData.SPEEDING -> trip.speedingStatistics?.score
            TripData.DISTANCE -> trip.tripStatistics?.distance
            TripData.DURATION -> trip.tripStatistics?.duration
        }

    override fun getTransportationModeResource(context: Context): Drawable? = null
    override fun isAlternative(): Boolean = false
    override fun infoText(context: Context) = if (trip.tripAdvices.size > 1) {
        DKSpannable().append("${trip.tripAdvices.size}", context.resSpans {
            color(DriveKitUI.colors.fontColorOnSecondaryColor())
            typeface(Typeface.BOLD)
            size(R.dimen.dk_text_very_small)
        }).toSpannable()
    } else {
        null
    }

    override fun infoImageResource(): Int? {
        val count = trip.tripAdvices.size
        if (count > 1) {
            return R.drawable.dk_common_trip_info_count
        } else if (count == 1) {
            val theme = trip.tripAdvices.first().theme
            if (theme == "SAFETY") {
                return R.drawable.dk_common_safety_advice
            } else if (theme == "ECODRIVING") {
                return R.drawable.dk_common_eco_advice
            }
        }
        return null
    }

    override fun infoClickAction(context: Context) {
        DriveKitNavigationController.driverDataUIEntryPoint?.startTripDetailActivity(
            context,
            itinId
        )
    }
    override fun hasInfoActionConfigured(): Boolean = true
    override fun isInfoDisplayable(): Boolean = !trip.tripAdvices.isNullOrEmpty()
}

internal fun List<Trip>.toDKTripList(): List<DKTripListItem> = this.map { it.toDKTripItem() }