package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import com.drivequant.drivekit.common.ui.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
//TODO ADD UNIT KEYS
//TODO HANDLE MISSING DURATION FORMAT
object DKUtils {

    fun formatDate(date: Date, pattern: String): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(date)
    }

    fun formatDuration(context: Context, duration: Double) : String {
        var formattedDuration = ""
        var computedDuration: Double? = duration

        if (computedDuration != null) {
            computedDuration = if (computedDuration % 60 > 0) {
                (((computedDuration / 60) * 60).toInt() + 60).toDouble()
            } else {
                (((computedDuration / 60) * 60).toInt()).toDouble()
            }

            val durationInt: Int = (computedDuration / 60).toInt()
            formattedDuration = if (durationInt > 60) {
                val minDuration: String = if (durationInt % 60 < 10) "0" + durationInt % 60 else (durationInt % 60).toString()
                String.format("%d%s%s", durationInt / 60, context.resources.getString(R.string.dk_common_unit_hour), minDuration)
            } else {
                String.format("%d %s", durationInt, context.resources.getString(R.string.dk_common_unit_minute))
            }
        }
        return formattedDuration
    }

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
            context.resources.getString(R.string.dk_common_unit_km))
    }

    fun formatCO2Emission(context: Context, emission: Double) :String = "${emission.roundToInt()} ${context.getString(
        R.string.dk_common_unit_hour)}"

    fun formatCO2Mass(context: Context, co2mass: Double): String {
        return if (co2mass < 1) {
            "${co2mass * 100} ${context.getString(R.string.dk_common_unit_meter)}"
        } else {
            "$co2mass ${context.getString(R.string.dk_common_unit_meter)}"
        }
    }

    fun formatSpeedMean(context: Context, speed: Double) : String = "${speed.roundToInt()} ${context.getString(
        R.string.dk_common_unit_hour)}"

    fun formatConsumption(context: Context, consumption: Double) :String =
        String.format(Locale.getDefault(),"%.1f",consumption,context.getString(R.string.dk_common_unit_minute))
}