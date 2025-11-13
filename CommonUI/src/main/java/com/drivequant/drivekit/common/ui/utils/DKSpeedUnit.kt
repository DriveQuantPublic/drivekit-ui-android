package com.drivequant.drivekit.common.ui.utils

@JvmInline
value class KilometerPerHour(val value: Double) {
    fun toMilePerHour(): MilePerHour {
        return if (value == 0.0) {
            MilePerHour(0.0)
        } else {
            MilePerHour(value / MILES_TO_KM_FACTOR)
        }
    }
}

@JvmInline
value class MilePerHour(val value: Double)