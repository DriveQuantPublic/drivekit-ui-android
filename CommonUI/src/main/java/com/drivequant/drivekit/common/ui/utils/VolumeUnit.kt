package com.drivequant.drivekit.common.ui.utils

const val LITERS_TO_GALLON_UK_FACTOR = 0.219969

@JvmInline
value class Liter(val value: Double) {
    fun toGallonUK(): GallonUk {
        return if (value == 0.0) {
            GallonUk(0.0)
        } else {
            GallonUk(value * LITERS_TO_GALLON_UK_FACTOR)
        }
    }
}

@JvmInline
value class GallonUk(val value: Double)