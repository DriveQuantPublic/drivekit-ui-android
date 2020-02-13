package com.drivequant.drivekit.driverachievement.ui.utils

import android.content.Context
import com.drivequant.drivekit.driverachievement.ui.R
import java.util.*

class DistanceUtils {
    fun formatDistance(context: Context, distance: Double): String {
        var format = "%.1f %s"
        val distanceInKm = (distance / 1000)
        if (distanceInKm <= 1 || distanceInKm >= 10) {
            format = "%.0f %s"
        }
        return String.format(
            Locale.getDefault(),
            format,
            distanceInKm,
            context.resources.getString(R.string.dk_unit_km)
        )
    }
}