package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKUnitSystem
import com.drivequant.drivekit.common.ui.utils.Meter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometer
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.R
import java.util.Calendar

internal class OdometerItemViewModel(val vehicleId: String) {

    private var vehicleOdometer: VehicleOdometer? = null

    init {
        vehicleOdometer =
            DriveKitVehicle.odometerQuery().whereEqualTo("vehicleId", vehicleId).queryOne()
                .executeOne()
    }

    fun getDescription(context: Context, odometerType: OdometerItemType) = when (odometerType) {
        OdometerItemType.ODOMETER -> getLastUpdateDate(context)
        OdometerItemType.ANALYZED -> getAnalyzedDistanceDescription(context)
        OdometerItemType.ESTIMATED -> getEstimatedDistanceDescription(context)
    }

    fun getDistance(context: Context, odometerType: OdometerItemType) = vehicleOdometer?.let {
        when (odometerType) {
            OdometerItemType.ODOMETER -> it.distance
            OdometerItemType.ANALYZED -> it.analyzedDistance
            OdometerItemType.ESTIMATED -> it.estimatedYearDistance
        }.let { distance ->
            getFormattedDistance(context, Meter(distance))
        }
    }

    private fun getEstimatedDistanceDescription(context: Context) = "${
        DKResource.buildString(
            context,
            DKColors.mainFontColor,
            DKColors.mainFontColor,
            R.string.dk_vehicle_odometer_estimated_distance_subtitle,
            "${Calendar.getInstance()[Calendar.YEAR]}"
        )
    }"

    private fun getAnalyzedDistanceDescription(context: Context): String {
        val analyzedDistance = vehicleOdometer?.let {
            DKDataFormatter.formatInKmOrMile(
                context, Meter(it.yearAnalyzedDistance * 1000),
                false, minDistanceToRemoveFractions = 0.0, forcedUnitSystem = DKUnitSystem.METRIC
            ).convertToString()
        } ?: ""

       return DKResource.buildString(
            context, DKColors.mainFontColor, DKColors.mainFontColor,
           R.string.dk_vehicle_odometer_distance_analyzed_subtitle, analyzedDistance
        ).toString()
    }

    private fun getLastUpdateDate(context: Context) = "${
        vehicleOdometer?.updateDate?.formatDate(
            DKDatePattern.STANDARD_DATE
        )?.let {
            DKResource.buildString(
                context,
                DKColors.mainFontColor,
                DKColors.mainFontColor,
                R.string.dk_vehicle_odometer_last_update,
                it
            )
        } ?: ""
    }"

    private fun getFormattedDistance(context: Context, meters: Meter) =
        DKDataFormatter.formatInKmOrMile(
            context,
            meters,
            minDistanceToRemoveFractions = 0.0,
            forcedUnitSystem = DKUnitSystem.METRIC
        ).convertToString()
}
