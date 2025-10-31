package com.drivequant.drivekit.common.ui.utils

internal const val LITERS_PER_100_KM_TO_MPG_FACTOR = 282.481053

@JvmInline
value class LitersPer100Kmh(val value: Double) {
    fun toMilesPerGallon() = MilesPerGallon(LITERS_PER_100_KM_TO_MPG_FACTOR / value)
}

@JvmInline
value class MilesPerGallon(val value: Double)