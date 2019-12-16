package com.drivequant.drivekit.ui.extension

import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.trips.viewmodel.TripsByDate
import com.drivequant.drivekit.ui.utils.DateUtils
import java.util.*

fun List<Trip>.computeTotalDistance() : Double {
    val iterator = this.listIterator()
    var totalDistance: Double = 0.toDouble()
    for (currentTrip in iterator){
       currentTrip.tripStatistics?.distance.let {
           if (it != null) {
               totalDistance += it
           }
       }
    }
    return totalDistance
}

fun List<Trip>.computeTotalDuration() : Double {
    val iterator = this.listIterator()
    var totalDuration: Double = 0.toDouble()
    for (currentTrip in iterator){
        currentTrip.tripStatistics?.duration.let {
            if (it != null) {
                totalDuration += it
            }
        }
    }
    return totalDuration
}

fun Trip.getOrComputeStartDate() : Date? {
    if (this.startDate != null){
        return this.startDate
    } else {
        this.tripStatistics?.duration?.let {
            return Date(this.endDate.time - (it * 1000).toLong())
        }
    }
    return null
}

fun List<Trip>.orderByDay(orderDesc: Boolean) : MutableList<TripsByDate> {
    val tripsSorted: MutableList<TripsByDate> = mutableListOf()
    if (this.isNotEmpty()){
        var dayTrips: MutableList<Trip> = mutableListOf()
        var currentDay: Date = this.first().endDate

        if (this.size > 1){
            for (i in this.indices){
                if (DateUtils().isSameDay(this[i], currentDay)){
                    dayTrips.add(this[i])
                    if (i == this.size -1) {
                        val tripsByDate = TripsByDate(currentDay, dayTrips)
                        tripsSorted.add(tripsByDate)
                    }
                } else {
                    if (!orderDesc){
                        dayTrips = dayTrips.asReversed()
                    }
                    val tripsByDate = TripsByDate(currentDay, dayTrips)
                    tripsSorted.add(tripsByDate)

                    currentDay = this[i].endDate
                    dayTrips = mutableListOf()
                    dayTrips.add(this[i])
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
    for (i in 0 until this.safetyContexts.size) {
        if (this.safetyContexts[i].distance > biggestDistance) {
            biggestDistance = this.safetyContexts[i].distance
            majorRoadContext = this.safetyContexts[i].contextId
        }
    }
    return if (majorRoadContext == 0) 1 else majorRoadContext
}