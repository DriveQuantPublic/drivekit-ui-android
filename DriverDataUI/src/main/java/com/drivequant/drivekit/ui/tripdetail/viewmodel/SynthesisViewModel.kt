package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.computeRoadContext
import java.io.Serializable

class SynthesisViewModel(private val trip: Trip) : Serializable {

    private val notAvailableText = "-"

    fun getRoadContextValue(context: Context): String? {
        return when (trip.computeRoadContext()){
            1 -> context.getString(R.string.dk_common_driving_context_city_dense)
            2 -> context.getString(R.string.dk_common_driving_context_city)
            3 -> context.getString(R.string.dk_common_driving_context_external)
            4 -> context.getString(R.string.dk_common_driving_context_fastlane)
            else -> context.getString(R.string.dk_driverdata_unknown)
        }
    }

    fun getWeaherValue(context: Context): String {
        return when (trip.tripStatistics?.meteo){
            1 -> context.getString(R.string.dk_driverdata_weather_sun)
            2 -> context.getString(R.string.dk_driverdata_weather_cloud)
            3 -> context.getString(R.string.dk_driverdata_weather_fog)
            4 -> context.getString(R.string.dk_driverdata_weather_rain)
            5 -> context.getString(R.string.dk_driverdata_weather_snow)
            6 -> context.getString(R.string.dk_driverdata_weather_hail)
            else -> context.getString(R.string.dk_driverdata_unknown)
        }
    }

    fun getCondition(context: Context): String {
        val isDay = trip.tripStatistics?.day
        return if (isDay != null){
            if(isDay){
                context.getString(R.string.dk_driverdata_day)
            } else {
                context.getString(R.string.dk_driverdata_night)
            }
        } else {
            context.getString(R.string.dk_driverdata_unknown)
        }
    }

    fun getCO2Mass(context: Context): String? = DKDataFormatter.formatCO2Mass(context, trip.fuelEstimation?.co2Mass!!)
//        val co2mass = trip.fuelEstimation?.co2Mass
//        return if (co2mass != null){
//            var formattedMass:Int
//            var massUnit = context.getString(R.string.dk_common_unit_kg)
//            if (co2mass < 1){
//                formattedMass = (co2mass*1000).toInt()
//                massUnit = context.getString(R.string.dk_common_unit_g)
//            } else {
//                formattedMass = co2mass.toInt()
//            }
//            "$formattedMass $massUnit"
//        } else {
//            notAvailableText
//        }

    fun getCo2Emission(context: Context): String? = DKDataFormatter.formatCO2Emission(context, trip.fuelEstimation?.co2Emission)
//        val co2emission = trip.fuelEstimation?.co2Emission
//        return if (co2emission != null){
//            String.format("%s %s", co2emission.roundToInt(), context.getString(R.string.dk_common_unit_g_per_km))
//        } else {
//            notAvailableText
//        }

    fun getFuelConsumption(context: Context): String? = DKDataFormatter.formatConsumption(context, trip.fuelEstimation?.fuelConsumption!!)
//        val fuelConsumption = trip.fuelEstimation?.fuelConsumption
//        return if (fuelConsumption != null){
//            String.format("%s %s", fuelConsumption, context.getString(R.string.dk_unit_liter_per_100km))
//        } else {
//            notAvailableText
//        }

    fun getIdlingDuration(context: Context): String = DKDataFormatter.formatDuration(context, trip.tripStatistics?.duration!!)
        // DurationUtils().formatDuration(context, trip.tripStatistics?.duration)


    fun getMeanSpeed(context: Context): String? = DKDataFormatter.formatSpeedMean(context, trip.tripStatistics?.speedMean)
//        val speedMean = trip.tripStatistics?.speedMean
//        return if (speedMean != null){
//            String.format("%s %s", speedMean.roundToInt(), context.getString(R.string.dk_unit_km_per_hour))
//        } else {
//            notAvailableText
//        }


    fun getVehicleDisplayName(): String {
        return notAvailableText
    }
}