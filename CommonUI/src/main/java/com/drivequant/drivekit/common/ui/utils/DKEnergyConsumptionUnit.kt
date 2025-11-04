package com.drivequant.drivekit.common.ui.utils

internal const val LITERS_PER_100_KM_TO_MPG_FACTOR = 282.481053

@JvmInline
value class LitersPer100Km(val value: Double) {
    fun toMilesPerGallon(): MilesPerGallon =
        if (value == 0.0) {
            MilesPerGallon(0.0)
        } else {
            MilesPerGallon(LITERS_PER_100_KM_TO_MPG_FACTOR / value)
        }
}

@JvmInline
value class MilesPerGallon(val value: Double)

@JvmInline
value class KWhPer100Km(val value: Double) {
    fun toMilePerKWh(): MilesPerKWh =
        if (value == 0.0) {
            MilesPerKWh(0.0)
        } else {
            MilesPerKWh(100 / MILES_TO_KM_FACTOR / value)
        }
}

@JvmInline
value class MilesPerKWh(val value: Double)