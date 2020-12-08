package com.drivequant.drivekit.ui.trips.viewmodel

import com.drivequant.drivekit.databaseutils.entity.TransportationMode

internal enum class TripListConfiguration {
    MOTORIZED,
    ALTERNATIVE;

    fun transportationModes(): List<TransportationMode> {
        return when (this) {
            MOTORIZED -> listOf(
                TransportationMode.UNKNOWN,
                TransportationMode.CAR,
                TransportationMode.MOTO,
                TransportationMode.TRUCK
            )
            ALTERNATIVE -> listOf(
                TransportationMode.BUS,
                TransportationMode.TRAIN,
                TransportationMode.BOAT,
                TransportationMode.BIKE,
                TransportationMode.FLIGHT,
                TransportationMode.SKIING,
                TransportationMode.ON_FOOT,
                TransportationMode.IDLE,
                TransportationMode.OTHER
            )
        }
    }
}