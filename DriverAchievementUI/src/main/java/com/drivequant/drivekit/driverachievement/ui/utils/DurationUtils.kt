package com.drivequant.drivekit.driverachievement.ui.utils

import android.content.Context
import com.drivequant.drivekit.driverachievement.ui.R

class DurationUtils {
    fun formatDuration(context: Context, duration: Double): String {
        var computedDuration: Double = duration
            computedDuration = if (computedDuration % 60 > 0) {
                (computedDuration.toInt() + 60).toDouble()
            } else {
                (computedDuration.toInt()).toDouble()
            }

            val durationInt: Int = (computedDuration / 60).toInt()
        return if (durationInt > 60) {
            val minDuration: String =
                if (durationInt % 60 < 10) "0" + durationInt % 60 else (durationInt % 60).toString()
            String.format(
                "%d%s%s",
                durationInt / 60,
                context.resources.getString(R.string.dk_unit_hour),
                minDuration
            )
        } else {
            String.format(
                "%d %s",
                durationInt,
                context.resources.getString(R.string.dk_unit_minute)
            )
        }
    }
}