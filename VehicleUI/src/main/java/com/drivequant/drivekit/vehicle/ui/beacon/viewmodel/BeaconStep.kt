package com.drivequant.drivekit.vehicle.ui.beacon.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R

enum class BeaconStep {
    INITIAL,
    SCAN,
    SUCCESS,
    BEACON_NOT_FOUND,
    BEACON_ALREADY_PAIRED,
    CONGRATS,
    BEACON_UNAVAILABLE,
    VERIFY,
    UNKNOWN;

    fun getTitle(context: Context): String? {
        val identifier = when (this) {
            INITIAL -> "dk_vehicle_beacon_setup_guide_title"
            SCAN -> "dk_vehicle_beacon_setup_guide_title"
            SUCCESS -> "dk_vehicle_beacon_setup_guide_title"
            BEACON_NOT_FOUND -> "dk_vehicle_beacon_setup_guide_title"
            BEACON_ALREADY_PAIRED -> "dk_vehicle_beacon_setup_guide_title"
            CONGRATS -> "dk_vehicle_beacon_setup_guide_title"
            BEACON_UNAVAILABLE -> "dk_vehicle_beacon_setup_guide_title"
            VERIFY -> "dk_vehicle_beacon_setup_guide_title"
            UNKNOWN -> "dk_vehicle_beacon_setup_guide_title"
        }
        return DKResource.convertToString(context, identifier)
    }

    fun getDescription(context: Context): String? {
        return "HC description mock"
    }

    fun getImage(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.dk_common_eco_maintain)
    }
}