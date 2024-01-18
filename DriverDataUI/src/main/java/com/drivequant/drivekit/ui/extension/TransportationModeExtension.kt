package com.drivequant.drivekit.ui.extension

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.ui.R

fun TransportationMode.image(context: Context): Drawable? =
    when (this) {
        TransportationMode.UNKNOWN,
        TransportationMode.CAR -> R.drawable.dk_transportation_car
        TransportationMode.MOTO -> R.drawable.dk_transportation_motorcycle
        TransportationMode.TRUCK -> R.drawable.dk_transportation_truck
        TransportationMode.BUS -> R.drawable.dk_transportation_bus
        TransportationMode.TRAIN -> R.drawable.dk_transportation_train
        TransportationMode.BOAT -> R.drawable.dk_transportation_boat
        TransportationMode.BIKE -> R.drawable.dk_transportation_bicycle
        TransportationMode.FLIGHT -> R.drawable.dk_transportation_plane
        TransportationMode.SKIING -> R.drawable.dk_transportation_skiing
        TransportationMode.ON_FOOT -> R.drawable.dk_transportation_on_foot
        TransportationMode.IDLE -> R.drawable.dk_transportation_idle
        TransportationMode.OTHER -> R.drawable.dk_transportation_other
    }.let {
        ContextCompat.getDrawable(context, it)
    }

fun TransportationMode?.text(context: Context): String =
    when (this) {
        TransportationMode.UNKNOWN -> null
        TransportationMode.CAR -> R.string.dk_driverdata_transportation_mode_car
        TransportationMode.MOTO -> R.string.dk_driverdata_transportation_mode_motorcycle
        TransportationMode.TRUCK -> R.string.dk_driverdata_transportation_mode_truck
        TransportationMode.BUS -> R.string.dk_driverdata_transportation_mode_bus
        TransportationMode.TRAIN -> R.string.dk_driverdata_transportation_mode_train
        TransportationMode.BOAT -> R.string.dk_driverdata_transportation_mode_boat
        TransportationMode.BIKE -> R.string.dk_driverdata_transportation_mode_bike
        TransportationMode.FLIGHT -> R.string.dk_driverdata_transportation_mode_flight
        TransportationMode.SKIING -> R.string.dk_driverdata_transportation_mode_skiing
        TransportationMode.ON_FOOT -> R.string.dk_driverdata_transportation_mode_on_foot
        TransportationMode.IDLE -> R.string.dk_driverdata_transportation_mode_idle
        TransportationMode.OTHER -> R.string.dk_driverdata_transportation_mode_other
        else -> null
    }?.let {
        context.getString(it)
    } ?: ""
