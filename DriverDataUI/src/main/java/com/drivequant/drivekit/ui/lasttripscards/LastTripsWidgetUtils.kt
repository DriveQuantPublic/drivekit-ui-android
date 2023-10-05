package com.drivequant.drivekit.ui.lasttripscards

import com.drivequant.drivekit.databaseutils.Query
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.databaseutils.entity.toTrips
import com.drivequant.drivekit.driverdata.DriveKitDriverData

internal object LastTripsWidgetUtils {

    @JvmOverloads
    fun getLastTrips(limit: Int, transportationModes: List<TransportationMode> = listOf(
            TransportationMode.UNKNOWN,
            TransportationMode.CAR,
            TransportationMode.MOTO,
            TransportationMode.TRUCK
        )): List<Trip> =
        DriveKitDriverData.tripsQuery()
        .whereIn("transportationMode", transportationModes.map { it.value })
        .orderBy("endDate", Query.Direction.DESCENDING).query().limit(limit).executeTrips()
        .toTrips()

    fun hasTrips() = DriveKitDriverData.tripsQuery().noFilter().query().limit(1).executeTrips().isNotEmpty()
}