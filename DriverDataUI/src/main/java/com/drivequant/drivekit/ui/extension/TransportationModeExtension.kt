package com.drivequant.drivekit.ui.extension

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.TransportationMode

fun TransportationMode.image(context: Context): Drawable? =
    when (this) {
        TransportationMode.UNKNOWN,
        TransportationMode.CAR -> "dk_transportation_car"
        TransportationMode.MOTO -> "dk_transportation_motorcycle"
        TransportationMode.TRUCK -> "dk_transportation_truck"
        TransportationMode.BUS -> "dk_transportation_bus"
        TransportationMode.TRAIN -> "dk_transportation_train"
        TransportationMode.BOAT -> "dk_transportation_boat"
        TransportationMode.BIKE -> "dk_transportation_bicycle"
        TransportationMode.FLIGHT -> "dk_transportation_plane"
        TransportationMode.SKIING -> "dk_transportation_skiing"
        TransportationMode.ON_FOOT -> "dk_transportation_on_foot"
        TransportationMode.IDLE -> "dk_transportation_idle"
        TransportationMode.OTHER -> "dk_transportation_other"
    }.let {
        DKResource.convertToDrawable(context, it)
    }

fun TransportationMode?.text(context: Context): String =
    when (this) {
        TransportationMode.UNKNOWN -> ""
        TransportationMode.CAR -> "dk_driverdata_transportation_mode_car"
        TransportationMode.MOTO -> "dk_driverdata_transportation_mode_motorcycle"
        TransportationMode.TRUCK -> "dk_driverdata_transportation_mode_truck"
        TransportationMode.BUS -> "dk_driverdata_transportation_mode_bus"
        TransportationMode.TRAIN -> "dk_driverdata_transportation_mode_train"
        TransportationMode.BOAT -> "dk_driverdata_transportation_mode_boat"
        TransportationMode.BIKE -> "dk_driverdata_transportation_mode_bike"
        TransportationMode.FLIGHT -> "dk_driverdata_transportation_mode_flight"
        TransportationMode.SKIING -> "dk_driverdata_transportation_mode_skiing"
        TransportationMode.ON_FOOT -> "dk_driverdata_transportation_mode_on_foot"
        TransportationMode.IDLE -> "dk_driverdata_transportation_mode_idle"
        TransportationMode.OTHER -> "dk_driverdata_transportation_mode_other"
        else -> ""
    }.let {
        DKResource.convertToString(context, it)
    }
