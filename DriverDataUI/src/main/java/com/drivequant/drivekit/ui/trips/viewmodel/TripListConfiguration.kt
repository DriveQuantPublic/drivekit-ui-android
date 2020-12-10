package com.drivequant.drivekit.ui.trips.viewmodel

import com.drivequant.drivekit.databaseutils.entity.TransportationMode

internal sealed class TripListConfiguration {
    data class MOTORIZED(val vehicleId: String? = null) : TripListConfiguration()
    data class ALTERNATIVE(val transportationMode: TransportationMode? = null) : TripListConfiguration()

    fun getTransportationModes(): List<TransportationMode> {
        return when (this) {
            is MOTORIZED -> listOf(
                TransportationMode.UNKNOWN,
                TransportationMode.CAR,
                TransportationMode.MOTO,
                TransportationMode.TRUCK
            )
            is ALTERNATIVE -> listOf(
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