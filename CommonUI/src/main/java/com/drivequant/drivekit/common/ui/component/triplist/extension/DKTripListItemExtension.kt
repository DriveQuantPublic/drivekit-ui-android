package com.drivequant.drivekit.common.ui.component.triplist.extension

import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.DKTripsByDate
import com.drivequant.drivekit.common.ui.extension.isSameDay
import com.drivequant.drivekit.core.geocoder.DKAddress
import java.util.*

fun DKTripListItem.getOrComputeStartDate(): Date? {
    if (this.getStartDate() != null) {
        return this.getStartDate()
    } else {
        this.getDuration()?.let {
            return Date(this.getEndDate().time - (it * 1000).toLong())
        }
    }
    return null
}

fun List<DKTripListItem>.computeCeilDuration(): Double {
    val iterator = this.listIterator()
    var totalDuration: Double = 0.toDouble()
    for (currentTrip in iterator) {
        totalDuration += currentTrip.computeCeilDuration().toInt()
    }
    return totalDuration
}

fun DKTripListItem.computeCeilDuration(): Double {
    this.getDuration()?.let {
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

fun List<DKTripListItem>.computeTotalDistance(): Double {
    val iterator = this.listIterator()
    var totalDistance: Double = 0.toDouble()
    for (currentTrip in iterator) {
        currentTrip.getDistance().let {
            if (it != null) {
                totalDistance += it
            }
        }
    }
    return totalDistance
}

fun List<DKTripListItem>.orderByDay(orderDesc: Boolean): MutableList<DKTripsByDate> {
    val tripsSorted: MutableList<DKTripsByDate> = mutableListOf()
    if (this.isNotEmpty()) {
        var dayTrips: MutableList<DKTripListItem> = mutableListOf()
        var currentDay: Date = this.first().getEndDate()

        if (this.size > 1) {
            for (i in this.indices) {
                if (this[i].getEndDate().isSameDay(currentDay)) {
                    dayTrips.add(this[i])
                } else {
                    if (orderDesc) {
                        dayTrips = dayTrips.asReversed()
                    }
                    val tripsByDate = DKTripsByDate(currentDay, dayTrips)
                    tripsSorted.add(tripsByDate)

                    currentDay = this[i].getEndDate()
                    dayTrips = mutableListOf()
                    dayTrips.add(this[i])
                }
                if (i == this.size - 1) {
                    tripsSorted.add(DKTripsByDate(currentDay, dayTrips))
                }
            }
        } else {
            dayTrips.add(this[0])
            val tripsByDate = DKTripsByDate(currentDay, dayTrips)
            tripsSorted.add(tripsByDate)
        }
    }
    return tripsSorted
}

fun DKTripListItem.computeDepartureInfo() =
    if (this.getDepartureCity().isBlank() || this.getDepartureCity() == DKAddress.unknownValue) {
        this.getDepartureAddress()
    } else {
        this.getDepartureCity()
    }

fun DKTripListItem.computeArrivalInfo() =
    if (this.getArrivalCity().isBlank() || this.getArrivalCity() == DKAddress.unknownValue) {
        this.getArrivalAddress()
    } else {
        this.getArrivalCity()
    }