package com.drivequant.drivekit.common.ui.component.tripslist


import com.drivequant.drivekit.common.ui.component.GaugeConfiguration

enum class TripData {
    ECO_DRIVING, SAFETY, DISTRACTION, SPEEDING, DURATION, DISTANCE;

    fun displayType(): DisplayType = when (this) {
        ECO_DRIVING, SAFETY, DISTRACTION, SPEEDING -> DisplayType.GAUGE
        DURATION, DISTANCE -> DisplayType.TEXT
    }

    fun getGaugeType(value: Double) : GaugeConfiguration =
        when (this) {
            ECO_DRIVING-> GaugeConfiguration.ECO_DRIVING(value)
            SAFETY-> GaugeConfiguration.SAFETY(value)
            DISTRACTION-> GaugeConfiguration.DISTRACTION(value)
            SPEEDING-> GaugeConfiguration.SPEEDING(value)
            else -> GaugeConfiguration.SAFETY(value)
    }
}

enum class DisplayType {
    GAUGE, TEXT
}