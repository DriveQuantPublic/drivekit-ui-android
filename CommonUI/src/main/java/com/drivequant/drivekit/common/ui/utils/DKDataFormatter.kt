package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.convertKmsToMiles
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.formatLeadingZero
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import kotlin.math.ceil
import kotlin.math.roundToInt

object DKDataFormatter {

    fun formatDuration(context: Context, durationInSeconds: Double?) : String {
        var nbMinute: Int
        var nbHour: Int
        val nbDay: Int

        if (durationInSeconds != null) {
            nbMinute = ceil(durationInSeconds.div(60)).toInt()
            return if (nbMinute > 59) {
                nbHour = nbMinute.div(60)
                nbMinute -= (nbHour * 60)

                if (nbHour > 23) {
                    nbDay = nbHour.div(24)
                    nbHour -= nbHour - (24 * nbDay)
                    "$nbDay ${context.getString(R.string.dk_common_unit_day)} $nbHour ${context.getString(
                        R.string.dk_common_unit_hour
                    )}"
                } else {
                    "$nbHour${context.getString(R.string.dk_common_unit_hour)}${nbMinute.formatLeadingZero()}"
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
            DistanceUnit.MILE -> {
                "${formatDistanceValue(distanceInMile)} ${context.resources.getString(R.string.dk_common_unit_mile)}"
            }
            DistanceUnit.KM -> {
                "${formatDistanceValue(distanceInKm)} ${context.resources.getString(R.string.dk_common_unit_kilometer)}"
            }
        }
    }

    private fun formatDistanceValue(distance: Double?): String? {
        return if (distance != null && distance >= 100) {
            distance.format(0)
        } else {
            distance?.removeZeroDecimal()
        }
    }

    fun formatCO2Emission(context: Context, emission: Double) : String = "${emission.roundToInt()} ${context.getString(
        R.string.dk_common_unit_g_per_km)}"

    fun formatCO2Mass(context: Context, co2mass: Double): String {
        return when {
            co2mass < 1 -> {
                val unit = DKResource.convertToString(context, "dk_common_unit_g")
                "${(co2mass * 1000).roundToInt()} $unit"
            }
            co2mass > 1000 -> {
                formatMassInTon(context, co2mass)
            }
            else -> {
                val unit = DKResource.convertToString(context, "dk_common_unit_kg")
                "${co2mass.format(2)} $unit"
            }
        }
    }

    fun formatSpeedMean(context: Context, speed: Double) : String = "${speed.roundToInt()} ${context.getString(
        R.string.dk_common_unit_km_per_hour)}"

    fun formatConsumption(context: Context, consumption: Double) : String =
        "${consumption.removeZeroDecimal()} ${context.getString(R.string.dk_common_unit_l_per_100km)}"

    fun formatMass(context: Context, mass: Double) : String {
        return "${mass.removeZeroDecimal()} ${context.getString(R.string.dk_common_unit_kg)}"
    }

    fun formatMassInTon(context: Context, mass: Double) : String {
        return "${(mass/1000).removeZeroDecimal()} ${context.getString(R.string.dk_common_unit_ton)}"
    }

    fun formatVehiclePower(context: Context, power: Double) : String =
        "${power.removeZeroDecimal()} ${context.getString(R.string.dk_common_unit_power)}"
}