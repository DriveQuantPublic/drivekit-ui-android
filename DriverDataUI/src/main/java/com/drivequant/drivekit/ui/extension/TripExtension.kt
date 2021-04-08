package com.drivequant.drivekit.ui.extension

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.component.tripslist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.tripslist.TripData
import com.drivequant.drivekit.common.ui.extension.isSameDay
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.viewmodel.TripsByDate
import java.util.*

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
        var computedDuration = it
        computedDuration = if (computedDuration % 60 > 0) {
            (computedDuration / 60).toInt() * 60 + 60.toDouble()
        } else {
            ((computedDuration / 60).toInt() * 60).toDouble()
        }
        return computedDuration
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

fun List<Trip>.orderByDay(orderDesc: Boolean): MutableList<TripsByDate> {
    val tripsSorted: MutableList<TripsByDate> = mutableListOf()
    if (this.isNotEmpty()) {
        var dayTrips: MutableList<Trip> = mutableListOf()
        var currentDay: Date = this.first().endDate

        if (this.size > 1) {
            for (i in this.indices) {
                if (this[i].endDate.isSameDay(currentDay)) {
                    dayTrips.add(this[i])
                } else {
                    if (orderDesc) {
                        dayTrips = dayTrips.asReversed()
                    }
                    val tripsByDate = TripsByDate(currentDay, dayTrips)
                    tripsSorted.add(tripsByDate)

                    currentDay = this[i].endDate
                    dayTrips = mutableListOf()
                    dayTrips.add(this[i])
                }
                if (i == this.size - 1) {
                    tripsSorted.add(TripsByDate(currentDay, dayTrips))
                }
            }
        } else {
            dayTrips.add(this[0])
            val tripsByDate = TripsByDate(currentDay, dayTrips)
            tripsSorted.add(tripsByDate)
        }
    }
    return tripsSorted
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
//TODO check if we can separate this implementation in a diffrent class
fun Trip.toDKTripItem() = object: DKTripListItem {
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

    override fun getTransportationModeResourceId(context: Context): Drawable? =
        trip.transportationMode.image(context)

    override fun isAlternative(): Boolean = trip.transportationMode.isAlternative()

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
                return R.drawable.dk_safety_advice
            } else if (theme == "ECODRIVING") {
                return R.drawable.dk_eco_advice
            }
        }
        return null
    }

    override fun infoClickAction(context: Context) {
        TripDetailActivity.launchActivity(
            context as Activity,
            trip.itinId,
            openAdvice = true
        )
    }
    override fun hasInfoActionConfigured(): Boolean = true
    override fun isDisplayable(): Boolean? = !trip.tripAdvices.isNullOrEmpty()
}

fun List<Trip>.toDKTripsList(): List<DKTripListItem> {
    val trips = mutableListOf<DKTripListItem>()
    this.forEach {
        trips.add(
            it.toDKTripItem()
        )
    }
    return trips
}