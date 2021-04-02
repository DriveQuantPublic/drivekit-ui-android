package com.drivequant.drivekit.ui.synthesiscards

import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.TripWithRelations
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import java.util.*


object SynthesisCardsUtils {

    @JvmOverloads
    fun getLastTrip(
        transportationModes: List<TransportationMode> = listOf(
            TransportationMode.UNKNOWN,
            TransportationMode.CAR,
            TransportationMode.MOTO,
            TransportationMode.TRUCK
        )
    ): TripWithRelations? {
        return DbTripAccess.findTripsOrderByDateDesc(transportationModes).executeTrips().firstOrNull()
    }

    @JvmOverloads
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
            cal.add(Calendar.HOUR, -24 * 7)
            trips.filter { it.trip.endDate.after(cal.time)}
        } else {
            listOf()
        }
    }
}