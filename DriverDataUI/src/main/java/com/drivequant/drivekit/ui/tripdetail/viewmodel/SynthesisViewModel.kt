package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.GetVehicleInfoByVehicleIdListener
import com.drivequant.drivekit.common.ui.utils.DKConsumptionType
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.computeRoadContext

class SynthesisViewModel(private val trip: Trip) : ViewModel() {

    var vehicleName: String? = null
    var liteConfig: Boolean? = null
    private val consumptionType: DKConsumptionType = trip.energyEstimation?.let {
        DKConsumptionType.ELECTRIC
    } ?: DKConsumptionType.FUEL

    private val notAvailableText = "-"

    fun init(context: Context) {
        trip.vehicleId?.let { vehicleId ->
            DriveKitNavigationController.vehicleUIEntryPoint?.getVehicleInfoById(context, vehicleId, object: GetVehicleInfoByVehicleIdListener {
                override fun onVehicleInfoRetrieved(vehicleName: String, liteConfig: Boolean?) {
                    this@SynthesisViewModel.vehicleName = vehicleName
                    this@SynthesisViewModel.liteConfig = liteConfig
                }
            })
        }
    }

    fun getRoadContextValue(context: Context): String {
        return when (trip.computeRoadContext()) {
            1 -> context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_city_dense)
            2 -> context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_city)
            3 -> context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_external)
            4 -> context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_fastlane)
            else -> context.getString(R.string.dk_driverdata_unknown)
        }
    }

    fun getWeatherValue(context: Context): String {
        return when (trip.tripStatistics?.meteo) {
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
        return if (isDay != null) {
            if (isDay) {
                context.getString(R.string.dk_driverdata_day)
            } else {
                context.getString(R.string.dk_driverdata_night)
            }
        } else {
            context.getString(R.string.dk_driverdata_unknown)
        }
    }

    fun getCO2Mass(context: Context): String {
        val co2mass = trip.fuelEstimation?.co2Mass
        return co2mass?.let {
            DKDataFormatter.formatCO2Mass(context, it)
        } ?: run {
            notAvailableText
        }
    }

    fun getCo2Emission(context: Context): String {
        val co2emission = trip.fuelEstimation?.co2Emission
        return co2emission?.let {
            DKDataFormatter.formatCO2Emission(context, it)
        } ?: run {
            notAvailableText
        }
    }

    private fun electricConsumptionValue(context: Context) =
        trip.energyEstimation?.energyConsumption?.let {
            DKDataFormatter.formatConsumption(context, it, DKConsumptionType.ELECTRIC)
                .convertToString()
        } ?: notAvailableText

    private fun fuelConsumptionValue(context: Context) = trip.fuelEstimation?.fuelConsumption?.let {
        DKDataFormatter.formatConsumption(context, it).convertToString()
    } ?: notAvailableText

    fun getConsumptionValue(context: Context) = when (consumptionType) {
        DKConsumptionType.FUEL -> fuelConsumptionValue(context)
        DKConsumptionType.ELECTRIC -> electricConsumptionValue(context)
    }

    fun getConsumptionTitle(context: Context) = when (consumptionType) {
        DKConsumptionType.FUEL -> R.string.dk_driverdata_synthesis_fuel_consumption
        DKConsumptionType.ELECTRIC -> R.string.dk_driverdata_synthesis_energy_consumption
    }.let { resourceId ->
        context.getString(resourceId)
    }

    fun getIdlingDuration(context: Context): String {
        return trip.tripStatistics?.let {
            DKDataFormatter.formatDuration(context, it.idlingDuration).convertToString()
        } ?: run {
            notAvailableText
        }
    }

    fun getMeanSpeed(context: Context): String {
        return trip.tripStatistics?.let {
            DKDataFormatter.formatSpeedMean(context, it.speedMean)
        } ?: run {
            notAvailableText
        }
    }

    fun getVehicleId() : String? {
        return trip.vehicleId
    }

    fun getVehicleDisplayName() : String {
        return vehicleName ?: notAvailableText
    }

    @Suppress("UNCHECKED_CAST")
    class SynthesisViewModelFactory(private val trip: Trip) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SynthesisViewModel(trip) as T
        }
    }
}
