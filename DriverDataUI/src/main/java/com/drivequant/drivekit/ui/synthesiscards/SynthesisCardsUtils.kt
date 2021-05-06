package com.drivequant.drivekit.ui.synthesiscards

import com.drivequant.drivekit.databaseutils.Query
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverdata.DriveKitDriverData
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
        val lastTrip =
            DriveKitDriverData.tripsQuery()
                .whereIn("transportationMode", transportationModes.map { it.value })
                .orderBy("endDate", Query.Direction.DESCENDING).queryOne().executeOne()
        return if (lastTrip != null) {
            val cal = Calendar.getInstance()
            cal.time = lastTrip.endDate
            cal.add(Calendar.HOUR, -24 * 7)
            DriveKitDriverData.tripsQuery()
                .whereIn("transportationMode", transportationModes.map { it.value })
                .and()
                .whereGreaterThan("endDate", cal.time).query().executeTrips().toTrips()
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