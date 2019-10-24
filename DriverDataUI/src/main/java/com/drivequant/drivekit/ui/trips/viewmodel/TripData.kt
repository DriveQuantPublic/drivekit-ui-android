package com.drivequant.drivekit.ui.trips.viewmodel

import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.commons.views.GaugeType
import com.drivequant.drivekit.ui.extension.lessOrEqualsThan
import com.drivequant.drivekit.ui.trips.viewmodel.DisplayType

enum class TripData {
    ECO_DRIVING, SAFETY, DISTRACTION, DURATION, DISTANCE;

    fun isScored(trip: Trip) : Boolean {
        return when (this){
            SAFETY, ECO_DRIVING, DISTRACTION -> rawValue(trip).lessOrEqualsThan(10.toDouble())
            DISTANCE, DURATION -> true
        }
    }

    fun displayType() : DisplayType {
        return when (this) {
            ECO_DRIVING, SAFETY, DISTRACTION -> DisplayType.GAUGE
            DURATION, DISTANCE -> DisplayType.TEXT
        }
    }

    fun getGaugeType() : GaugeType {
        return when(this){
            ECO_DRIVING -> GaugeType.ECO_DRIVING
            SAFETY -> GaugeType.SAFETY
            DISTRACTION -> GaugeType.DISTRACTION
            else -> GaugeType.SAFETY
        }
    }

    fun rawValue(trip: Trip) : Double? {
        return when (this){
            SAFETY -> trip.safety?.safetyScore
            ECO_DRIVING -> trip.ecoDriving?.score
            DISTRACTION -> trip.driverDistraction?.score
            DISTANCE -> trip.tripStatistics?.distance
            DURATION -> trip.tripStatistics?.duration
        }
    }
}

