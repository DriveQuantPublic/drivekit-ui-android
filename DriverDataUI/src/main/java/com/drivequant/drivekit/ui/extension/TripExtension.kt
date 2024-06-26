package com.drivequant.drivekit.ui.extension

import android.app.Activity
import android.content.Context
import android.graphics.Typeface.BOLD
import android.graphics.drawable.Drawable
import android.text.Spannable
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.extension.ceilDuration
import com.drivequant.drivekit.common.ui.extension.formatDateWithPattern
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import java.util.Date

fun List<Trip>.computeAverageScore(scoreType: DKScoreType): Double =
    if (this.isEmpty()) {
        11.0
    } else {
        val tripsWithScore = when (scoreType) {
            DKScoreType.SAFETY -> this.mapNotNull { it.safety?.safetyScore }
            DKScoreType.ECO_DRIVING -> this.mapNotNull { it.ecoDriving?.score }
            DKScoreType.DISTRACTION -> this.mapNotNull { it.driverDistraction?.score }
            DKScoreType.SPEEDING -> this.mapNotNull { it.speedingStatistics?.score }
        }
        if (tripsWithScore.isEmpty()) {
            11.0
        } else {
            tripsWithScore.sum().div(tripsWithScore.size)
        }
    }

fun List<Trip>.computeActiveDays(): Int {
    val simpleDateFormat = DKDatePattern.STANDARD_DATE.getSimpleDateFormat()
    return this.distinctBy { it.endDate.formatDateWithPattern(simpleDateFormat) }.size
}

fun List<Trip>.computeTotalDistance(): Double {
    val iterator = this.listIterator()
    var totalDistance: Double = 0.toDouble()
    for (currentTrip in iterator) {
        currentTrip.tripStatistics?.distance.let {
            if (it != null) {
                totalDistance += it
            }
        }
    }
    return totalDistance
}

fun List<Trip>.computeTotalDuration(): Double {
    val iterator = this.listIterator()
    var totalDuration: Double = 0.toDouble()
    for (currentTrip in iterator) {
        currentTrip.tripStatistics?.duration.let {
            if (it != null) {
                totalDuration += it
            }
        }
    }
    return totalDuration
}

fun List<Trip>.computeCeilDuration(): Double {
    val iterator = this.listIterator()
    var totalDuration: Double = 0.toDouble()
    for (currentTrip in iterator) {
        totalDuration += currentTrip.computeCeilDuration().toInt()
    }
    return totalDuration
}

fun Trip.computeCeilDuration(): Double {
    this.tripStatistics?.duration?.let {
        return it.ceilDuration()
    } ?: run {
        return 0.0
    }
}

fun Trip.getOrComputeStartDate(): Date? {
    if (this.startDate != null) {
        return this.startDate
    } else {
        this.tripStatistics?.duration?.let {
            return Date(this.endDate.time - (it * 1000).toLong())
        }
    }
    return null
}

fun Trip.computeRoadContext(): Int {
    var biggestDistance = 0.0
    var majorRoadContext = 0
    for (i in this.safetyContexts.indices) {
        if (this.safetyContexts[i].distance > biggestDistance) {
            biggestDistance = this.safetyContexts[i].distance
            majorRoadContext = this.safetyContexts[i].contextId
        }
    }
    return if (majorRoadContext == 0) 1 else majorRoadContext
}

internal fun Trip.toDKTripItem() = object : DKTripListItem {
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

    override fun getTransportationModeResource(context: Context): Drawable? =
        trip.declaredTransportationMode?.transportationMode?.image(context) ?: run {
            trip.transportationMode.image(context)
        }

    override fun isAlternative(): Boolean = trip.transportationMode.isAlternative()

    override fun infoText(context: Context): Spannable? {
        DriverDataUI.customTripInfo?.let {
            return it.infoText(context, trip)
        } ?: run {
            return if (trip.tripAdvices.size > 1) {
                DKSpannable().append("${trip.tripAdvices.size}", context.resSpans {
                    color(DKColors.fontColorOnSecondaryColor)
                    typeface(BOLD)
                    size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_very_small)
                }).toSpannable()
            } else {
                null
            }
        }
    }

    override fun infoImageResource(): Int? {
        return DriverDataUI.customTripInfo?.infoImageResource(trip) ?: run {
            val count = trip.tripAdvices.size
            if (count > 1) {
                return com.drivequant.drivekit.common.ui.R.drawable.dk_common_trip_info_count
            } else if (count == 1) {
                val theme = trip.tripAdvices.first().theme
                if (theme == "SAFETY") {
                    return com.drivequant.drivekit.common.ui.R.drawable.dk_common_safety_advice
                } else if (theme == "ECODRIVING") {
                    return com.drivequant.drivekit.common.ui.R.drawable.dk_common_eco_advice
                }
            }
            return null
        }
    }

    override fun infoClickAction(context: Context) {
        return DriverDataUI.customTripInfo?.infoClickAction(context, trip) ?: run {
            TripDetailActivity.launchActivity(
                context as Activity,
                trip.itinId,
                openAdvice = true
            )
        }
    }

    override fun hasInfoActionConfigured(): Boolean =
        DriverDataUI.customTripInfo?.hasInfoActionConfigured(trip) ?: false

    override fun isInfoDisplayable(): Boolean =
        (DriverDataUI.customTripInfo?.isInfoDisplayable(trip)
            ?: trip.tripAdvices.isNotEmpty()) && !trip.transportationMode.isAlternative()
}

internal fun List<Trip>.toDKTripList(): List<DKTripListItem> = this.map { it.toDKTripItem() }
