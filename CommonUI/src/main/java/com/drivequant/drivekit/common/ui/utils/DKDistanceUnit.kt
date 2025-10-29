package com.drivequant.drivekit.common.ui.utils

internal const val MILES_TO_KM_FACTOR = 1.609344

@JvmInline
value class Kilometer(val value: Double) {
    fun toMiles() = Mile(value / MILES_TO_KM_FACTOR)
}

@JvmInline
value class Meter(val value: Double) {
    fun toKilometers() = Kilometer(value / 1000)
}

@JvmInline
value class Mile(val value: Double)