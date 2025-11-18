package com.drivequant.drivekit.common.ui.utils

const val MILES_TO_KM_FACTOR = 1.609344

@JvmInline
value class Kilometer(val value: Double) {
    fun toMiles(): Mile {
        return Mile(value / MILES_TO_KM_FACTOR)
    }
    fun toMeters() = Meter(value * 1000)
}

@JvmInline
value class Meter(val value: Double) {
    fun toKilometers(): Kilometer {
        return Kilometer(value / 1000)
    }
}

@JvmInline
value class Mile(val value: Double) {
    fun toKilometers() = Kilometer(value * MILES_TO_KM_FACTOR)
    fun toMeters() = this.toKilometers().toMeters()
}