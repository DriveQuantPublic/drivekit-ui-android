package com.drivekit.demoapp.dashboard.enum

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drivekit.demoapp.config.DriveKitConfig
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.databaseutils.Query
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.driverdata.trip.driverpassengermode.DriverPassengerMode
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI

internal enum class InfoBannerType {
    DIAGNOSIS,
    DRIVER_PASSENGER;

    fun shouldDisplay(context: Context) = when (this) {
        DIAGNOSIS -> PermissionsUtilsUI.hasError(context)
        DRIVER_PASSENGER -> {
            if (DriveKitConfig.hasUserAlreadyOpenedDriverPassengerMode(context)) {
                false
            } else {
                val hasTripsDetectedAsPassenger = DbTripAccess.tripsQuery()
                    .whereEqualTo("OccupantInfo_role", DriverPassengerMode.PASSENGER.name)
                    .orderBy("endDate", Query.Direction.DESCENDING)
                    .countQuery().execute() > 0
                hasTripsDetectedAsPassenger
            }
        }
    }

    @ColorInt
    fun getColor() = when (this) {
        DIAGNOSIS,
        DRIVER_PASSENGER -> Color.WHITE
    }

    @ColorInt
    fun getBackgroundColor() = when (this) {
        DIAGNOSIS -> DKColors.warningColor
        DRIVER_PASSENGER -> DKColors.informationColor
    }

    @DrawableRes
    fun getIconResId() = when (this) {
        DIAGNOSIS -> com.drivequant.drivekit.permissionsutils.R.drawable.dk_perm_utils_diagnosis_system
        DRIVER_PASSENGER -> com.drivequant.drivekit.common.ui.R.drawable.dk_passenger_declaration
    }

    @StringRes
    fun getTitleResId() = when (this) {
        DIAGNOSIS -> R.string.info_banner_diagnosis_title
        DRIVER_PASSENGER -> R.string.info_banner_passenger_declaration_title
    }

    fun displayArrow() = when (this) {
        DIAGNOSIS,
        DRIVER_PASSENGER -> true
    }
}
