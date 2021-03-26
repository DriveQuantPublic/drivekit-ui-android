package com.drivequant.drivekit.ui.trips.viewmodel

import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.databaseutils.entity.Trip

enum class TripData {
    ECO_DRIVING, SAFETY, DISTRACTION, SPEEDING, DURATION, DISTANCE;

    fun isScored(trip: Trip): Boolean = when (this) {
        SAFETY, ECO_DRIVING -> !trip.unscored
        DISTRACTION -> !trip.unscored && trip.driverDistraction != null
        SPEEDING -> !trip.unscored && trip.speedingStatistics != null
        DISTANCE, DURATION -> true
    }

    fun displayType(): DisplayType = when (this) {
        ECO_DRIVING, SAFETY, DISTRACTION, SPEEDING -> DisplayType.GAUGE
        DURATION, DISTANCE -> DisplayType.TEXT
    }

    fun getGaugeType(): GaugeConfiguration = when (this) {
        ECO_DRIVING -> GaugeConfiguration.ECO_DRIVING
        SAFETY -> GaugeConfiguration.SAFETY
        DISTRACTION -> GaugeConfiguration.DISTRACTION
        SPEEDING -> GaugeConfiguration.SPEEDING
        else -> GaugeConfiguration.SAFETY
    }

    fun rawValue(trip: Trip): Double? = when (this) {
        SAFETY -> trip.safety?.safetyScore
        ECO_DRIVING -> trip.ecoDriving?.score
        DISTRACTION -> trip.driverDistraction?.score
        SPEEDING -> trip.speedingStatistics?.score
        DISTANCE -> trip.tripStatistics?.distance
        DURATION -> trip.tripStatistics?.duration
    }
}