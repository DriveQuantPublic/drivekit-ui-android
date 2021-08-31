package com.drivequant.drivekit.ui.lasttripscards

import com.drivequant.drivekit.databaseutils.Query
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.databaseutils.entity.toTrips
import com.drivequant.drivekit.driverdata.DriveKitDriverData

internal object LastTripsWidgetUtils {

    fun getLastTrips(limit: Int): List<Trip> = DriveKitDriverData.tripsQuery()
        .whereIn("transportationMode", listOf(0, 1, 2, 3))
        .orderBy("endDate", Query.Direction.DESCENDING).query().limit(limit).executeTrips()
        .toTrips()
}