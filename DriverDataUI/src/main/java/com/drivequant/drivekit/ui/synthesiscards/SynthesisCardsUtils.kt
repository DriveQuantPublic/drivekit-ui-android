package com.drivequant.drivekit.ui.synthesiscards

import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.ui.commons.enums.DKRoadCondition
import com.drivequant.drivekit.ui.commons.model.RoadConditionStats
import java.util.*

object SynthesisCardsUtils {

    @JvmOverloads
    fun getLastWeekTrips(
        transportationModes: List<TransportationMode> = listOf(
            TransportationMode.UNKNOWN,
            TransportationMode.CAR,
            TransportationMode.MOTO,
            TransportationMode.TRUCK
        )
    ): List<Trip> {
        val trips = DbTripAccess.findTripsOrderByDateDesc(transportationModes).executeTrips().toTrips()
        return if (trips.isNotEmpty()) {
            val cal = Calendar.getInstance()
            cal.time = trips.first().endDate
            cal.add(Calendar.HOUR, -24 * 7)
            trips.filter { it.endDate.after(cal.time)}
        } else {
            listOf()
        }
    }

    fun getMainRoadCondition(trips: List<Trip>, type: RoadConditionType) : Pair<DKRoadCondition, Double> {
        val roadConditionStats = RoadConditionStats(type, trips).compute()
        return Pair(roadConditionStats.first, roadConditionStats.second)
    }

    enum class RoadConditionType {
        SAFETY,
        ECO_DRIVING
    }
}