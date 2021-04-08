package com.drivequant.drivekit.common.ui.component.tripslist.extension

import com.drivequant.drivekit.common.ui.component.tripslist.DKTripListItem
import java.util.*

fun List<DKTripListItem>.computeTotalDuration(): Double {
    val iterator = this.listIterator()
    var totalDuration: Double = 0.toDouble()
    for (currentTrip in iterator) {
        currentTrip.getDuration().let {
            if (it != null) {
                totalDuration += it
            }
        }
    }
    return totalDuration
}

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