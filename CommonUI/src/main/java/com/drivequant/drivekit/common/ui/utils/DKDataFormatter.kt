package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.convertKmsToMiles
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import java.util.*
import kotlin.math.roundToInt

object DKDataFormatter {

    fun formatDuration(context: Context, durationInSeconds: Double?): String {

        var nbMinute: Int
        var nbHour: Int
        val nbDay: Int

        if (durationInSeconds != null) {
            if (durationInSeconds > 60) {
                nbMinute = (durationInSeconds / 60).roundToInt()
            } else {
                return "${durationInSeconds.toInt()} ${context.getString(R.string.dk_common_unit_second)}"
            }

            return if (nbMinute > 59) {
                nbHour = nbMinute / 60
                nbMinute -= (nbHour * 60)

                if (nbHour > 23) {
                    nbDay = nbHour / 24
                    nbHour -= nbHour - (24 * nbDay)
                    "$nbDay ${context.getString(R.string.dk_common_unit_day)} $nbHour ${context.getString(
                        R.string.dk_common_unit_hour
                    )}"
                } else {
                    "$nbHour ${context.getString(R.string.dk_common_unit_hour)} $nbMinute"
                }
            } else {
                "$nbMinute ${context.getString(R.string.dk_common_unit_minute)}"
            }
        }
        return "-"
    }

    fun formatDistance(context: Context, distance: Double?): String {
        val distanceInKm = distance?.div(1000)
        val distanceInMile = distanceInKm?.convertKmsToMiles()

        return when (DriveKitUI.distanceUnit) {
            DistanceUnit.MILE ->
                "${distanceInMile?.removeZeroDecimal()} ${context.resources.getString(R.string.dk_common_unit_mile)}"

            DistanceUnit.KM ->
                "${distanceInKm?.removeZeroDecimal()} ${context.resources.getString(R.string.dk_common_unit_kilometer)}"
        }
    }

    fun formatCO2Emission(context: Context, emission: Double) :String = "${emission.roundToInt()} ${context.getString(
        R.string.dk_common_unit_g_per_km)}"

    fun formatCO2Mass(context: Context, co2mass: Double): String {
        return if (co2mass < 1) {
            "${(co2mass * 1000).toInt()} ${context.getString(R.string.dk_common_unit_g)}"
        } else {
            "$co2mass ${context.getString(R.string.dk_common_unit_kg)}"
        }
    }

    fun formatSpeedMean(context: Context, speed: Double) : String = "${speed.roundToInt()} ${context.getString(
        R.string.dk_common_unit_km_per_hour)}"

    fun formatConsumption(context: Context, consumption: Double) :String =
        String.format(Locale.getDefault(), "%.1f %s", consumption, context.getString(R.string.dk_common_unit_l_per_100km))
}