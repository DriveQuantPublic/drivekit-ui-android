package com.drivequant.drivekit.ui.extension

import com.drivequant.drivekit.common.ui.extension.ceilDuration
import com.drivequant.drivekit.common.ui.extension.formatDateWithPattern
import com.drivequant.drivekit.common.ui.extension.isSameDay
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.trips.viewmodel.TripsByDate
import java.text.SimpleDateFormat
import java.util.*

fun List<Trip>.computeSafetyScoreAverage(): Double {
    val scoredTrips = this.filter { it.safety?.safetyScore != null }
    return if (this.isEmpty() || scoredTrips.isEmpty()){
        11.0
    } else {
        val sumScore = scoredTrips.mapNotNull { it.safety?.safetyScore }.sum()
        sumScore / scoredTrips.size
    }
}

fun List<Trip>.computeEcodrivingScoreAverage(): Double {
    val scoredTrips = this.filter { it.ecoDriving?.score != null }
    return if (this.isEmpty() || scoredTrips.isEmpty()) {
        11.0
    } else {
        val sumScore = scoredTrips.mapNotNull { it.ecoDriving?.score }.sum()
        sumScore / scoredTrips.size
    }
}

fun List<Trip>.computeDistractionScoreAverage(): Double {
    return if (this.isEmpty()) {
        11.0
    } else {
        val sumScore = this.mapNotNull { it.driverDistraction?.score }.sum()
        sumScore / this.size
    }
}

fun List<Trip>.computeSpeedingScoreAverage(): Double {
    return if (this.isEmpty()) {
        11.0
    } else {
        val sumScore = this.mapNotNull { it.speedingStatistics?.score }.sum()
        sumScore / this.size
    }
}

fun List<Trip>.computeActiveDays(): Int {
    val sdf = SimpleDateFormat(DKDatePattern.STANDARD_DATE.getPattern(), Locale.getDefault())
    return this.distinctBy { it.endDate.formatDateWithPattern(sdf) }.size
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