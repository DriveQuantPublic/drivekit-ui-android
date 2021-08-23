package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.convertKmsToMiles
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.formatLeadingZero
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.text.Typography.nbsp

object DKDataFormatter {

    @JvmOverloads
    fun formatDuration(
        context: Context,
        durationInSeconds: Double?,
        maxUnit: DurationUnit = DurationUnit.DAY
    ): List<FormatType> {
        var nbMinute: Int
        var nbHour: Int
        val nbDay: Int

        val formattingTypes = mutableListOf<FormatType>()
        if (durationInSeconds != null) {
            if (maxUnit != DurationUnit.SECOND && durationInSeconds > 59) {
                nbMinute = ceil(durationInSeconds.div(60)).toInt()
            } else {
                formattingTypes.addAll(
                    listOf(
                        FormatType.VALUE(durationInSeconds.toInt().toString()),
                        FormatType.SEPARATOR(),
                        FormatType.UNIT(context.getString(R.string.dk_common_unit_second))
                    )
                )
                return formattingTypes
            }
            if (maxUnit != DurationUnit.MINUTE && nbMinute > 59) {
                nbHour = nbMinute.div(60)
                nbMinute -= (nbHour * 60)

                if (maxUnit != DurationUnit.HOUR && nbHour > 23) {
                    nbDay = nbHour.div(24)
                    nbHour -= (24 * nbDay)
                    if (nbHour > 0) {
                        formattingTypes.addAll(
                            listOf(
                                FormatType.VALUE(nbDay.toString()),
                                FormatType.SEPARATOR(),
                                FormatType.UNIT(context.getString(R.string.dk_common_unit_day)),
                                FormatType.SEPARATOR(),
                                FormatType.VALUE(nbHour.toString()),
                                FormatType.SEPARATOR(),
                                FormatType.UNIT(context.getString(R.string.dk_common_unit_hour))
                            )
                        )
                    } else {
                        formattingTypes.addAll(
                            listOf(
                                FormatType.VALUE(nbDay.toString()),
                                FormatType.SEPARATOR(),
                                FormatType.UNIT(context.getString(R.string.dk_common_unit_day))
                            )
                        )
                    }
                    return formattingTypes
                } else {
                    if (nbMinute > 0) {
                        formattingTypes.addAll(
                            listOf(
                                FormatType.VALUE(nbHour.toString()),
                                FormatType.SEPARATOR(),
                                FormatType.UNIT(context.getString(R.string.dk_common_unit_hour)),
                                FormatType.SEPARATOR(),
                                FormatType.VALUE(nbMinute.formatLeadingZero())
                            )
                        )
                    } else {
                        formattingTypes.addAll(
                            listOf(
                                FormatType.VALUE(nbHour.toString()),
                                FormatType.SEPARATOR(),
                                FormatType.UNIT(context.getString(R.string.dk_common_unit_hour))
                            )
                        )
                    }
                    return formattingTypes
                }
            } else {
                val nbSecond =
                    (durationInSeconds - 60 * ((durationInSeconds / 60).toInt()).toDouble()).toInt()
                if (nbSecond > 0) {
                    val nbMinutes = nbMinute - 1
                    formattingTypes.addAll(
                        listOf(
                            FormatType.VALUE(nbMinutes.toString()),
                            FormatType.SEPARATOR(),
                            FormatType.UNIT(context.getString(R.string.dk_common_unit_minute)),
                            FormatType.SEPARATOR(),
                            FormatType.VALUE(nbSecond.formatLeadingZero())
                        )
                    )
                } else {
                    formattingTypes.addAll(
                        listOf(
                            FormatType.VALUE(nbMinute.toString()),
                            FormatType.SEPARATOR(),
                            FormatType.UNIT(context.getString(R.string.dk_common_unit_minute))
                        )
                    )
                }
                return formattingTypes
            }
        }
        formattingTypes.add(FormatType.VALUE("-"))
        return formattingTypes
    }

    fun formatExactDuration(context: Context, durationInMilliSecond: Long): List<FormatType> {
        val formattingTypes = mutableListOf<FormatType>()
        var difference = durationInMilliSecond
        val secondsInMilli = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val days = difference / daysInMilli
        difference %= daysInMilli
        if (days > 0) {
            formattingTypes.addAll(
                listOf(
                    FormatType.VALUE(days.toString()),
                    FormatType.SEPARATOR(),
                    FormatType.UNIT(context.getString(R.string.dk_common_unit_day)),
                    FormatType.SEPARATOR()
                )
            )
        }
        val hours = difference / hoursInMilli
        difference %= hoursInMilli
        if (hours > 0) {
            formattingTypes.addAll(
                listOf(
                    FormatType.VALUE(hours.toString()),
                    FormatType.SEPARATOR(),
                    FormatType.UNIT(context.getString(R.string.dk_common_unit_hour)),
                    FormatType.SEPARATOR()
                )
            )
        }
        val minutes = difference / minutesInMilli
        difference %= minutesInMilli
        if (minutes > 0) {
            formattingTypes.addAll(
                listOf(
                    FormatType.VALUE(minutes.toString()),
                    FormatType.SEPARATOR(),
                    FormatType.UNIT(context.getString(R.string.dk_common_unit_minute)),
                    FormatType.SEPARATOR()
                )
            )
        }
        val seconds = difference / secondsInMilli
        formattingTypes.addAll(
            listOf(
                FormatType.VALUE(seconds.toString()),
                FormatType.SEPARATOR(),
                FormatType.UNIT(context.getString(R.string.dk_common_unit_second)),
                FormatType.SEPARATOR()
            )
        )
        return formattingTypes
    }

    @JvmOverloads
    fun getMeterDistanceInKmFormat(
        context: Context,
        value: Double?,
        unit: Boolean = true
    ): List<FormatType> {
        return getKilometerDistanceFormat(context, value?.div(1000), unit)
    }

    @JvmOverloads
    fun getMeterDistanceFormat(
        context: Context,
        value: Double?,
        unit: Boolean = true): List<FormatType> {
        lateinit var formattingTypes: List<FormatType>

        value?.let {
            when {
                value < 10 -> {
                    formattingTypes = listOf(
                        FormatType.VALUE(value.removeZeroDecimal().format(2)),
                        FormatType.SEPARATOR(),
                        FormatType.UNIT(context.getString(R.string.dk_common_unit_meter))
                    )
                }
                value < 1000 -> {
                    formattingTypes = listOf(
                        FormatType.VALUE(value.format(0)),
                        FormatType.SEPARATOR(),
                        FormatType.UNIT(context.getString(R.string.dk_common_unit_meter))
                    )
                }
                else -> {
                    formattingTypes = getMeterDistanceInKmFormat(context, value, unit)
                }
            }
        }
        return formattingTypes
    }

    @JvmOverloads
    fun getKilometerDistanceFormat(
        context: Context,
        value: Double?,
        unit: Boolean = true
    ): List<FormatType> {
        val formattingTypes = mutableListOf<FormatType>()
        var formattedDistance: String
        value?.let {
            formattedDistance = if (value < 100) {
                BigDecimal(it).setScale(1, RoundingMode.HALF_EVEN).toDouble().removeZeroDecimal()
            } else {
                value.roundToInt().toString()
            }
            formattingTypes.add(FormatType.VALUE(formattedDistance))
            if (unit) {
                formattingTypes.add(FormatType.SEPARATOR())
                formattingTypes.add(FormatType.UNIT(context.resources.getString(R.string.dk_common_unit_kilometer)))
            }
        }
        return formattingTypes
    }

    @JvmOverloads
    fun formatMeterDistanceInKm(context: Context, distance: Double?, unit: Boolean = true, minDistanceToRemoveFractions: Double = 100.0): List<FormatType> {
        val distanceInKm = distance?.div(1000)
        val distanceInMile = distanceInKm?.convertKmsToMiles()

        val formattingTypes = mutableListOf<FormatType>()

        when (DriveKitUI.distanceUnit) {
            DistanceUnit.MILE -> {
                formattingTypes.add(FormatType.VALUE(formatDistanceValue(distanceInMile, minDistanceToRemoveFractions) ?: ""))
                if (unit) {
                    formattingTypes.add(FormatType.SEPARATOR())
                    formattingTypes.add(FormatType.UNIT(context.getString(R.string.dk_common_unit_mile)))
                }
            }
            DistanceUnit.KM -> {
                formattingTypes.add(FormatType.VALUE(formatDistanceValue(distanceInKm, minDistanceToRemoveFractions) ?: ""))
                if (unit) {
                    formattingTypes.add(FormatType.SEPARATOR())
                    formattingTypes.add(FormatType.UNIT(context.getString(R.string.dk_common_unit_kilometer)))
                }
            }
        }
        return formattingTypes
    }

    fun formatMeterDistance(context: Context, distance: Double?, unit: Boolean = true): List<FormatType> {
        distance?.let {
            return when {
                it == 0.0 -> {
                    listOf(
                        FormatType.VALUE(it.removeZeroDecimal()),
                        FormatType.SEPARATOR(),
                        FormatType.UNIT(context.getString(R.string.dk_common_unit_meter))
                    )
                }
                it < 10 -> {
                    listOf(
                        FormatType.VALUE(it.format(2)),
                        FormatType.SEPARATOR(),
                        FormatType.UNIT(context.getString(R.string.dk_common_unit_meter))
                    )
                }
                it < 1000 -> {
                    listOf(
                        FormatType.VALUE(it.format(0)),
                        FormatType.SEPARATOR(),
                        FormatType.UNIT(context.getString(R.string.dk_common_unit_meter))
                    )
                }
                else -> {
                    formatMeterDistanceInKm(context, distance, unit)
                }
            }
        } ?: run {
            return listOf(
                FormatType.VALUE("-")
            )
        }
    }

    private fun formatDistanceValue(distance: Double?, minDistanceRemoveFraction: Double): String? {
        distance?.let {
            val roundedDistance = BigDecimal(distance).setScale(2, RoundingMode.UP).toDouble()
            return if (roundedDistance >= minDistanceRemoveFraction) {
                roundedDistance.format(0)
            } else {
                roundedDistance.removeZeroDecimal()
            }
        }
        return null
    }

    fun formatCO2Emission(context: Context, emission: Double) : String =
        "${emission.roundToInt()}"
            .plus(nbsp)
            .plus(context.getString(R.string.dk_common_unit_g_per_km))

    fun formatCO2Mass(context: Context, co2mass: Double): String {
        return when {
            co2mass < 1 -> {
                val unit = DKResource.convertToString(context, "dk_common_unit_g")
                "${(co2mass * 1000).roundToInt()}"
                    .plus(nbsp)
                    .plus(unit)
            }
            co2mass > 1000 -> {
                formatMassInTon(context, co2mass)
            }
            else -> {
                val unit = DKResource.convertToString(context, "dk_common_unit_kg")
                co2mass.format(2)
                    .plus(nbsp)
                    .plus(unit)
            }
        }
    }

    fun formatSpeedMean(context: Context, speed: Double): String =
        "${speed.roundToInt()}"
            .plus(nbsp)
            .plus(context.getString(R.string.dk_common_unit_km_per_hour))

    fun formatConsumption(context: Context, consumption: Double): String =
        consumption.removeZeroDecimal()
            .plus(nbsp)
            .plus(context.getString(R.string.dk_common_unit_l_per_100km))

    fun formatMass(context: Context, mass: Double): String =
        mass.removeZeroDecimal()
            .plus(nbsp)
            .plus(context.getString(R.string.dk_common_unit_kg))


    fun formatMassInTon(context: Context, mass: Double): String =
        (mass / 1000).removeZeroDecimal()
            .plus(nbsp)
            .plus(context.getString(R.string.dk_common_unit_ton))

    fun formatVehiclePower(context: Context, power: Double): String =
        power.removeZeroDecimal()
            .plus(nbsp)
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