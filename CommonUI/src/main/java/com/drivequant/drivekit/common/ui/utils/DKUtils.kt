package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import com.drivequant.drivekit.common.ui.DKUIConfig
import com.drivequant.drivekit.common.ui.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object DKUtils {

    fun formatDate(date: Date, pattern: String): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(date)
    }

    fun formatDuration(context: Context, durationInSeconds: Double): String {
        var computedDuration: Double =  durationInSeconds

        computedDuration = if (computedDuration % 60 > 0) {
            (((computedDuration / 60) * 60).toInt() + 60).toDouble()
        } else {
            (((computedDuration / 60) * 60).toInt()).toDouble()
        }

        val durationInt: Int = (computedDuration / 60).toInt()

        return if (durationInt > 60) {
            val minDuration: String = if (durationInt % 60 < 10) "0" + durationInt % 60 else (durationInt % 60).toString()
            String.format("%d%s%s", durationInt / 60, context.resources.getString(R.string.dk_common_unit_hour), minDuration)
        } else {
            String.format("%d %s", durationInt, context.resources.getString(R.string.dk_common_unit_minute))
        }
    }

    fun formatDistance(context: Context, distance: Double): String {
        val distanceInKm = distance / 1000
        val distanceInMile = convertKmsToMiles(distanceInKm)

        return when (DKUIConfig.distanceUnit) {
            DistanceUnit.MILE ->
                "${DecimalFormat("0.#").format(distanceInMile)} ${context.resources.getString(R.string.dk_common_unit_mile)}"

            DistanceUnit.KM ->
                "${DecimalFormat("0.#").format(distanceInKm)} ${context.resources.getString(R.string.dk_common_unit_km)}"
        }
    }

    fun formatCO2Emission(context: Context, emission: Double) :String = "${emission.roundToInt()} ${context.getString(
        R.string.dk_common_unit_co2_emissions)}"

    fun formatCO2Mass(context: Context, co2mass: Double): String {
        return if (co2mass < 1) {
            "${co2mass * 100} ${context.getString(R.string.dk_common_unit_mass)}"
        } else {
            "$co2mass ${context.getString(R.string.dk_common_unit_mass_g)}"
        }
    }

    fun formatSpeedMean(context: Context, speed: Double) : String = "${speed.roundToInt()} ${context.getString(
        R.string.dk_common_unit_speed)}"

    fun formatConsumption(context: Context, consumption: Double) :String =
        String.format(Locale.getDefault(), "%.1f", consumption, context.getString(R.string.dk_common_unit_consumption))

    private fun convertKmsToMiles(kms: Double): Double = 0.621371 * kms

    fun readCSVFile(context: Context, ressourceId: Int) {

    }
}