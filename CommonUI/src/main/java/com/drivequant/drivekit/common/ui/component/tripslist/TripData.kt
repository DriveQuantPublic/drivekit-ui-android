package com.drivequant.drivekit.common.ui.component.tripslist

import com.drivequant.drivekit.common.ui.component.GaugeType

enum class TripData {
    ECO_DRIVING, SAFETY, DISTRACTION, SPEEDING, DURATION, DISTANCE;

    fun displayType(): DisplayType = when (this) {
        ECO_DRIVING, SAFETY, DISTRACTION, SPEEDING -> DisplayType.GAUGE
        DURATION, DISTANCE -> DisplayType.TEXT
    }

    fun getGaugeType(): GaugeType = when (this) {
        ECO_DRIVING -> GaugeType.ECO_DRIVING
        SAFETY -> GaugeType.SAFETY
        DISTRACTION -> GaugeType.DISTRACTION
        SPEEDING -> GaugeType.SPEEDING
        else -> GaugeType.SAFETY
    }
}

enum class DisplayType {
    GAUGE, TEXT
}