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
            var formattedDuration = ""
            var computedDuration: Double? = durationInSeconds

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
            "${co2mass.toInt()} ${context.getString(R.string.dk_common_unit_kg)}"
        }
    }

    fun formatSpeedMean(context: Context, speed: Double) : String = "${speed.roundToInt()} ${context.getString(
        R.string.dk_common_unit_km_per_hour)}"

    fun formatConsumption(context: Context, consumption: Double) :String =
        String.format(Locale.getDefault(), "%.1f %s", consumption, context.getString(R.string.dk_common_unit_l_per_100km))
}