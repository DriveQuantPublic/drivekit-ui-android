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

    private const val NON_BREAKING_SPACE = "\u00A0"

    fun formatDuration(context: Context, durationInSeconds: Double?) : String {
        var nbMinute: Int
        var nbHour: Int
        val nbDay: Int

        if (durationInSeconds != null) {
            if (durationInSeconds > 59) {
                nbMinute = ceil(durationInSeconds.div(60)).toInt()
            } else {
                return durationInSeconds.toInt().toString()
                    .plus(NON_BREAKING_SPACE)
                    .plus(context.getString(R.string.dk_common_unit_second))
            }
            return if (nbMinute > 59) {
                nbHour = nbMinute.div(60)
                nbMinute -= (nbHour * 60)

                if (nbHour > 23) {
                    nbDay = nbHour.div(24)
                    nbHour -= nbHour - (24 * nbDay)
                    "$nbDay"
                        .plus(context.getString(R.string.dk_common_unit_day))
                        .plus(NON_BREAKING_SPACE)
                        .plus(nbHour)
                        .plus(context.getString(R.string.dk_common_unit_hour))
                } else {
                    "$nbHour${context.getString(R.string.dk_common_unit_hour)}${nbMinute.formatLeadingZero()}"
                }
            } else {
                val nbSecond = (durationInSeconds - 60 * ((durationInSeconds / 60).toInt()).toDouble()).toInt()
                return if (nbSecond > 0) {
                    "${nbMinute - 1}"
                        .plus(context.getString(R.string.dk_common_unit_minute))
                        .plus(nbSecond.formatLeadingZero())
                } else {
                    "$nbMinute"
                        .plus(NON_BREAKING_SPACE)
                        .plus(context.getString(R.string.dk_common_unit_minute))
                }
            }
        }
        return "-"
    }

    fun formatMeterDistanceInKm(context: Context, distance: Double?, unit: Boolean = true): String {
        val distanceInKm = distance?.div(1000)
        val distanceInMile = distanceInKm?.convertKmsToMiles()

        return when (DriveKitUI.distanceUnit) {
            DistanceUnit.MILE -> {
                if (unit) "${formatDistanceValue(distanceInMile)}"
                    .plus(NON_BREAKING_SPACE)
                    .plus(context.resources.getString(R.string.dk_common_unit_mile))
                else "${formatDistanceValue(distanceInMile)}"
            }
            DistanceUnit.KM -> {
                if (unit) "${formatDistanceValue(distanceInKm)}"
                    .plus(NON_BREAKING_SPACE)
                    .plus(context.resources.getString(R.string.dk_common_unit_kilometer))
                else "${formatDistanceValue(distanceInKm)}"
            }
        }
    }

    fun formatMeterDistance(context: Context, distance: Double?, unit: Boolean = true): String {
        distance?.let {
            return when {
                it == 0.0 -> {
                    it.removeZeroDecimal()
                        .plus(NON_BREAKING_SPACE)
                        .plus(context.getString(R.string.dk_common_unit_meter))
                }
                it < 10 -> {
                    it.format(2)
                        .plus(NON_BREAKING_SPACE)
                        .plus(context.getString(R.string.dk_common_unit_meter))
                }
                it < 1000 -> {
                    it.format(0)
                        .plus(NON_BREAKING_SPACE)
                        .plus(context.getString(R.string.dk_common_unit_meter))
                }
                else -> {
                    formatMeterDistanceInKm(context, distance, unit)
                }
            }
        } ?: run {
            return "-"
        }
    }

    private fun formatDistanceValue(distance: Double?): String? {
        return if (distance != null && distance >= 100) {
            distance.format(0)
        } else {
            distance?.removeZeroDecimal()
        }
    }

    fun formatCO2Emission(context: Context, emission: Double) : String =
        "${emission.roundToInt()}"
            .plus(NON_BREAKING_SPACE)
            .plus(context.getString(R.string.dk_common_unit_g_per_km))

    fun formatCO2Mass(context: Context, co2mass: Double): String {
        return when {
            co2mass < 1 -> {
                val unit = DKResource.convertToString(context, "dk_common_unit_g")
                "${(co2mass * 1000).roundToInt()}"
                    .plus(NON_BREAKING_SPACE)
                    .plus(unit)
            }
            co2mass > 1000 -> {
                formatMassInTon(context, co2mass)
            }
            else -> {
                val unit = DKResource.convertToString(context, "dk_common_unit_kg")
                co2mass.format(2)
                    .plus(NON_BREAKING_SPACE)
                    .plus(unit)
            }
        }
    }

    fun formatSpeedMean(context: Context, speed: Double): String =
        "${speed.roundToInt()}"
            .plus(NON_BREAKING_SPACE)
            .plus(context.getString(R.string.dk_common_unit_km_per_hour))

    fun formatConsumption(context: Context, consumption: Double): String =
        consumption.removeZeroDecimal()
            .plus(NON_BREAKING_SPACE)
            .plus(context.getString(R.string.dk_common_unit_l_per_100km))

    fun formatMass(context: Context, mass: Double): String =
        mass.removeZeroDecimal()
            .plus(NON_BREAKING_SPACE)
            .plus(context.getString(R.string.dk_common_unit_kg))


    fun formatMassInTon(context: Context, mass: Double): String =
        (mass / 1000).removeZeroDecimal()
            .plus(NON_BREAKING_SPACE)
            .plus(context.getString(R.string.dk_common_unit_ton))

    fun formatVehiclePower(context: Context, power: Double): String =
        power.removeZeroDecimal()
            .plus(NON_BREAKING_SPACE)
            .plus(context.getString(R.string.dk_common_unit_power))

    fun ceilDuration(durationInSeconds: Double?, ceilValueInSeconds: Int): Double? {
        return if (durationInSeconds != null && durationInSeconds.rem(60) > 0 && durationInSeconds >= ceilValueInSeconds) {
            durationInSeconds.div(60).toInt() * 60.toDouble() + 60
        } else {
            durationInSeconds
        }
    }

    fun ceilDistance(distanceInMeter: Double?, ceilValueInMeter: Int): Double? {
        return if (distanceInMeter != null && distanceInMeter.rem(1000) > 0 && distanceInMeter >= ceilValueInMeter) {
            distanceInMeter.div(1000).toInt() * 1000.toDouble() + 1000
        } else {
            distanceInMeter
        }
    }
}