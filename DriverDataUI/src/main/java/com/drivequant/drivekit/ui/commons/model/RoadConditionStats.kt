package com.drivequant.drivekit.ui.commons.model

import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.commons.enums.DKRoadCondition
import com.drivequant.drivekit.ui.synthesiscards.SynthesisCardsUtils

class RoadConditionStats(
    var roadConditionType: SynthesisCardsUtils.RoadConditionType,
    var trips: List<Trip>
) {
    fun compute(): Pair<DKRoadCondition, Double> {
        val tripsCount = trips.size
        var majorRoadCondition = DKRoadCondition.TRAFFIC_JAM
        var tripCountMax = 0

        if (tripsCount > 0){
            val mainRoadConditionCount: HashMap<DKRoadCondition, Int> = hashMapOf()
            trips.forEach { trip ->
                if (trip.ecoDrivingContexts.isNotEmpty() && trip.safetyContexts.isNotEmpty()) {
                    var majorRoadType = DKRoadCondition.TRAFFIC_JAM
                    var distanceMajor = 0.0
                    for (i in DKRoadCondition.values().indices) {
                        if (DKRoadCondition.getTypeFromValue(i) == DKRoadCondition.TRAFFIC_JAM) {
                            continue
                        }
                        val myDistance: Double = if (roadConditionType == SynthesisCardsUtils.RoadConditionType.SAFETY) {
                            trip.safetyContexts[i].distance
                        } else {
                            trip.ecoDrivingContexts[i].distance
                        }
                        if (myDistance > distanceMajor) {
                            majorRoadType = if (roadConditionType == SynthesisCardsUtils.RoadConditionType.SAFETY) {
                                DKRoadCondition.getTypeFromValue(trip.safetyContexts[i].contextId)
                            } else {
                                DKRoadCondition.getTypeFromValue(trip.ecoDrivingContexts[i].contextId)
                            }
                            if (majorRoadType != DKRoadCondition.TRAFFIC_JAM){
                                distanceMajor = myDistance
                            }
                        }
                    }

                    if (majorRoadType != DKRoadCondition.TRAFFIC_JAM) {
                        val currentCount = mainRoadConditionCount[majorRoadType]
                        if (currentCount != null) {
                            mainRoadConditionCount[majorRoadType] = currentCount + 1
                        } else {
                            mainRoadConditionCount[majorRoadType] = 1
                        }
                    }
                }
            }

            mainRoadConditionCount.forEach {
                if (it.value > tripCountMax) {
                    majorRoadCondition = it.key
                    tripCountMax = it.value
                }
            }
        }
        val percent = (tripCountMax.toDouble() / tripsCount.toDouble() * 100.0)
        return Pair(majorRoadCondition, percent)
    }
}