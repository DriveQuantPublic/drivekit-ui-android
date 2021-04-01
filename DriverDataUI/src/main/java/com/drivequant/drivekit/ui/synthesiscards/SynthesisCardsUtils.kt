package com.drivequant.drivekit.ui.synthesiscards

import com.drivequant.drivekit.databaseutils.entity.DBTrip
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.TripWithRelations
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import java.util.*


class SynthesisCardsUtils {

    @JvmOverloads
    fun getLastTrip(
        transportationModes: List<TransportationMode> = listOf(
            TransportationMode.UNKNOWN,
            TransportationMode.CAR,
            TransportationMode.MOTO,
            TransportationMode.TRUCK
        )
    ): TripWithRelations? {
        val trips = DbTripAccess.findTripsOrderByDateDesc(transportationModes).executeTrips()
        return if (trips.isNotEmpty()) {
            trips.first()
        } else {
            null
        }
    }

    fun getLastWeekTrips(
        transportationModes: List<TransportationMode> = listOf(
            TransportationMode.UNKNOWN,
            TransportationMode.CAR,
            TransportationMode.MOTO,
            TransportationMode.TRUCK
        )
    ): List<TripWithRelations> {
        val trips = DbTripAccess.findTripsOrderByDateDesc(transportationModes).executeTrips()
        return if (trips.isNotEmpty()) {
            val cal = Calendar.getInstance()
            cal.time = trips.first().trip.endDate
            cal.add(Calendar.HOUR, - 24 * 7)
            val computedTrips = DbTripAccess.tripsQuery().whereGreaterThan("endDate", cal.time).query().executeTrips()
            computedTrips
        } else {
            listOf()
        }
    }
}