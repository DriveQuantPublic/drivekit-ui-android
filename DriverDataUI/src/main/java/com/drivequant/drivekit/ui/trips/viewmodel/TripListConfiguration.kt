package com.drivequant.drivekit.ui.trips.viewmodel

import com.drivequant.drivekit.databaseutils.entity.TransportationMode

internal sealed class TripListConfiguration {
    data class MOTORIZED(val vehicleId: String? = null) : TripListConfiguration()
    data class ALTERNATIVE(val transportationMode: TransportationMode? = null) : TripListConfiguration()
}