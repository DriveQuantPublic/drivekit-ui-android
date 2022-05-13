package com.drivekit.demoapp.utils

import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.databaseutils.entity.TransportationMode

internal fun TransportationMode.isAlternativeNotificationManaged() = this.getAlternativeNotificationBodyResId() != null

internal fun TransportationMode.getAlternativeNotificationBodyResId() = when (this) {
    TransportationMode.TRAIN -> R.string.notif_trip_train_detected
    TransportationMode.BOAT -> R.string.notif_trip_boat_detected
    TransportationMode.BIKE -> R.string.notif_trip_bike_detected
    TransportationMode.SKIING -> R.string.notif_trip_skiing_detected
    TransportationMode.IDLE -> R.string.notif_trip_idle_detected
    TransportationMode.UNKNOWN,
    TransportationMode.CAR,
    TransportationMode.MOTO,
    TransportationMode.TRUCK,
    TransportationMode.BUS,
    TransportationMode.FLIGHT,
    TransportationMode.ON_FOOT,
    TransportationMode.OTHER -> null
}