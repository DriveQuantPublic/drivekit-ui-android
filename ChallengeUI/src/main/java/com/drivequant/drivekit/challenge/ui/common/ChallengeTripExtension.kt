package com.drivequant.drivekit.challenge.ui.common

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.databaseutils.entity.Trip
import java.util.*

internal fun Trip.toDKTripItem() = object: DKTripListItem {
    val trip = this@toDKTripItem
    override fun getItinId(): String = trip.itinId
    override fun getDuration(): Double? = trip.tripStatistics?.duration
    override fun getDistance(): Double? = trip.tripStatistics?.distance
    override fun getStartDate(): Date? = trip.startDate
    override fun getEndDate(): Date = trip.endDate
    override fun getDepartureCity(): String = trip.departureCity
    override fun getArrivalCity(): String = trip.arrivalCity
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
    override fun infoText(): String? {
        return if (trip.tripAdvices.size > 1) {
            trip.tripAdvices.size.toString()
        } else {
            null
        }
    }

    override fun infoImageResource(): Int? {
        val count = trip.tripAdvices.size
        if (count > 1) {
            return R.drawable.dk_trip_info_count
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