package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometer
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import java.util.*

class OdometerItemViewModel(val vehicleId: String) {

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

    fun getDistance(context: Context, odometerType: OdometerItemType) = when (odometerType) {
        OdometerItemType.ODOMETER -> getMileageDistance(context)
        OdometerItemType.ANALYZED -> getAnalyzedDistance(context)
        OdometerItemType.ESTIMATED -> getEstimatedAnnualDistance(context)
    }

    private fun getEstimatedDistanceDescription(context: Context) = "${
        DKResource.buildString(
            context,
            DriveKitUI.colors.mainFontColor(),
            DriveKitUI.colors.mainFontColor(),
            "dk_vehicle_odometer_estimated_distance_subtitle",
            "${Calendar.getInstance()[Calendar.YEAR]}"
        )
    }"

    private fun getAnalyzedDistanceDescription(context: Context): String {
        val analyzedDistance = vehicleOdometer?.yearAnalyzedDistance?.let {
            DKDataFormatter.formatMeterDistance(
                context,
                it * 1000,
                false
            ).convertToString()
        } ?: run {
            ""
        }

       return DKResource.buildString(
            context, DriveKitUI.colors.mainFontColor(), DriveKitUI.colors.mainFontColor(),
            "dk_vehicle_odometer_distance_analyzed_subtitle", analyzedDistance
        ).toString()
    }

    private fun getLastUpdateDate(context: Context) = "${
        vehicleOdometer?.updateDate?.formatDate(
            DKDatePattern.STANDARD_DATE
        )?.let {
            DKResource.buildString(
                context,
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.mainFontColor(),
                "dk_vehicle_odometer_last_update",
                it
            )
        } ?: run {
            ""
        }
    }"

    private fun getMileageDistance(context: Context) = vehicleOdometer?.distance?.let {
        DKDataFormatter.formatMeterDistanceInKm(context, it * 1000).convertToString()
    } ?: run {
        ""
    }

    private fun getAnalyzedDistance(context: Context) = vehicleOdometer?.analyzedDistance?.let {
        DKDataFormatter.formatMeterDistanceInKm(
            context,
            it * 1000
        ).convertToString()
    } ?: run {
        ""
    }

    private fun getEstimatedAnnualDistance(context: Context) =
        vehicleOdometer?.estimatedYearDistance?.let {
            DKDataFormatter.formatMeterDistanceInKm(
                context,
                it * 1000
            ).convertToString()
        } ?: run {
            ""
        }
}