package com.drivequant.drivekit.timeline.ui

import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.AllContextItem
import com.drivequant.drivekit.databaseutils.entity.RoadContextItem
import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.toTimelineRoadContext


private fun Timeline.hasValidTripScored(selectedScore: DKScoreType, selectedIndex: Int) =
    selectedScore == DKScoreType.DISTRACTION
            || selectedScore == DKScoreType.SPEEDING
            || this.allContext.numberTripScored[selectedIndex, 0] > 0

internal fun Timeline.hasData(): Boolean {
    return this.allContext.numberTripTotal.isNotEmpty()
}

internal fun Timeline.distanceByRoadContext(selectedScore: DKScoreType, selectedIndex: Int): Map<TimelineRoadContext, Double> {
    val distanceByContext = mutableMapOf<TimelineRoadContext, Double>()
    if (this.hasValidTripScored(selectedScore, selectedIndex)) {
        this.roadContexts.forEach {
            val distance = it.distance[selectedIndex, 0.0]
            if (distance > 0) {
                distanceByContext[it.type.toTimelineRoadContext()] = distance
            }
        }
    }
    return distanceByContext
}

internal fun Timeline.totalDistanceForAllContexts(selectedScore: DKScoreType, selectedIndex: Int) : Double {
    var totalDistanceForAllContexts = 0.0
    if (this.hasValidTripScored(selectedScore, selectedIndex)) {
        totalDistanceForAllContexts = this.allContext.distance[selectedIndex, 0.0]
    }
    return totalDistanceForAllContexts
}

internal fun Timeline.cleanedTimeline(score: DKScoreType, selectedDateIndex: Int?): Timeline {
    val date = mutableListOf<String>()
    val numberTripTotal = mutableListOf<Int>()
    val numberTripScored = mutableListOf<Int>()
    val distance = mutableListOf<Double>()
    val duration = mutableListOf<Int>()
    val efficiency = mutableListOf<Double>()
    val safety = mutableListOf<Double>()
    val acceleration = mutableListOf<Int>()
    val braking = mutableListOf<Int>()
    val adherence = mutableListOf<Int>()
    val phoneDistraction = mutableListOf<Double>()
    val speeding = mutableListOf<Double>()
    val co2Mass = mutableListOf<Double>()
    val fuelVolume = mutableListOf<Double>()
    val fuelSaving = mutableListOf<Double>()
    val unlock = mutableListOf<Int>()
    val lock = mutableListOf<Int>()
    val callAuthorized = mutableListOf<Int>()
    val callForbidden = mutableListOf<Int>()
    val callForbiddenDuration = mutableListOf<Int>()
    val callAuthorizedDuration = mutableListOf<Int>()
    val numberTripWithForbiddenCall = mutableListOf<Int>()
    val speedingDuration = mutableListOf<Int>()
    val speedingDistance = mutableListOf<Double>()
    val efficiencyBrake = mutableListOf<Double>()
    val efficiencyAcceleration = mutableListOf<Double>()
    val efficiencySpeedMaintain = mutableListOf<Double>()

    val allContextItem = this.allContext

    val canInsertAtIndex: (Int) -> Boolean = { pos ->
        this.allContext.numberTripScored[pos] > 0
                || score == DKScoreType.DISTRACTION
                || score == DKScoreType.SPEEDING
                || selectedDateIndex == pos
    }

    allContextItem.date.forEachIndexed { index, _ ->
        val currentDate = allContextItem.date.getSafe(index)
        if (currentDate != null && canInsertAtIndex(index)) {
            date.add(currentDate)
            allContextItem.numberTripTotal.addValueIfNotEmpty(index, numberTripTotal)
            allContextItem.numberTripScored.addValueIfNotEmpty(index, numberTripScored)
            allContextItem.distance.addValueIfNotEmpty(index, distance)
            allContextItem.duration.addValueIfNotEmpty(index, duration)
            allContextItem.efficiency.addValueIfNotEmpty(index, efficiency)
            allContextItem.safety.addValueIfNotEmpty(index, safety)
            allContextItem.acceleration.addValueIfNotEmpty(index, acceleration)
            allContextItem.braking.addValueIfNotEmpty(index, braking)
            allContextItem.adherence.addValueIfNotEmpty(index, adherence)
            allContextItem.phoneDistraction.addValueIfNotEmpty(index, phoneDistraction)
            allContextItem.speeding.addValueIfNotEmpty(index, speeding)
            allContextItem.co2Mass.addValueIfNotEmpty(index, co2Mass)
            allContextItem.fuelVolume.addValueIfNotEmpty(index, fuelVolume)
            allContextItem.fuelSaving.addValueIfNotEmpty(index, fuelSaving)
            allContextItem.unlock.addValueIfNotEmpty(index, unlock)
            allContextItem.lock.addValueIfNotEmpty(index, lock)
            allContextItem.callAuthorized.addValueIfNotEmpty(index, callAuthorized)
            allContextItem.callForbidden.addValueIfNotEmpty(index, callForbidden)
            allContextItem.callForbiddenDuration.addValueIfNotEmpty(index, callForbiddenDuration)
            allContextItem.callAuthorizedDuration.addValueIfNotEmpty(index, callAuthorizedDuration)
            allContextItem.numberTripWithForbiddenCall.addValueIfNotEmpty(index, numberTripWithForbiddenCall)
            allContextItem.speedingDuration.addValueIfNotEmpty(index, speedingDuration)
            allContextItem.speedingDistance.addValueIfNotEmpty(index, speedingDistance)
            allContextItem.efficiencyBrake.addValueIfNotEmpty(index, efficiencyBrake)
            allContextItem.efficiencyAcceleration.addValueIfNotEmpty(index, efficiencyAcceleration)
            allContextItem.efficiencySpeedMaintain.addValueIfNotEmpty(index, efficiencySpeedMaintain)
        }
    }

    val roadContexts = mutableListOf<RoadContextItem>()
    this.roadContexts.forEachIndexed { _, roadContextItem ->
        val date = mutableListOf<String>()
        val numberTripTotal = mutableListOf<Int>()
        val numberTripScored = mutableListOf<Int>()
        val distance = mutableListOf<Double>()
        val duration = mutableListOf<Int>()
        val efficiency = mutableListOf<Double>()
        val safety = mutableListOf<Double>()
        val acceleration = mutableListOf<Int>()
        val braking = mutableListOf<Int>()
        val adherence = mutableListOf<Int>()
        val co2Mass = mutableListOf<Double>()
        val fuelVolume = mutableListOf<Double>()
        val fuelSaving = mutableListOf<Double>()
        val efficiencyAcceleration = mutableListOf<Double>()
        val efficiencyBrake = mutableListOf<Double>()
        val efficiencySpeedMaintain = mutableListOf<Double>()

        this.allContext.date.forEachIndexed { index, _ ->
            val currentDate = roadContextItem.date.getSafe(index)
            if (currentDate != null && canInsertAtIndex(index)) {
                date.add(currentDate)
                roadContextItem.numberTripTotal.addValueIfNotEmpty(index, numberTripTotal)
                roadContextItem.numberTripScored.addValueIfNotEmpty(index, numberTripScored)
                roadContextItem.distance.addValueIfNotEmpty(index, distance)
                roadContextItem.duration.addValueIfNotEmpty(index, duration)
                roadContextItem.efficiency.addValueIfNotEmpty(index, efficiency)
                roadContextItem.safety.addValueIfNotEmpty(index, safety)
                roadContextItem.acceleration.addValueIfNotEmpty(index, acceleration)
                roadContextItem.braking.addValueIfNotEmpty(index, braking)
                roadContextItem.adherence.addValueIfNotEmpty(index, adherence)
                roadContextItem.co2Mass.addValueIfNotEmpty(index, co2Mass)
                roadContextItem.fuelVolume.addValueIfNotEmpty(index, fuelVolume)
                roadContextItem.fuelSaving.addValueIfNotEmpty(index, fuelSaving)
                roadContextItem.efficiencyAcceleration.addValueIfNotEmpty(index, efficiencyAcceleration)
                roadContextItem.efficiencyBrake.addValueIfNotEmpty(index, efficiencyBrake)
                roadContextItem.efficiencySpeedMaintain.addValueIfNotEmpty(index, efficiencySpeedMaintain)
            }
        }

        val newRoadContext = RoadContextItem(
            roadContextItem.type,
            date,
            numberTripTotal,
            numberTripScored,
            distance,
            duration,
            efficiency,
            safety,
            acceleration,
            braking,
            adherence,
            co2Mass,
            fuelVolume,
            fuelSaving,
            efficiencyAcceleration,
            efficiencyBrake,
            efficiencySpeedMaintain
        )
        roadContexts.add(newRoadContext)
    }

    val allContext = AllContextItem(
        date,
        numberTripScored,
        numberTripTotal,
        distance,
        duration,
        efficiency,
        safety,
        acceleration,
        braking,
        adherence,
        phoneDistraction,
        speeding,
        co2Mass,
        fuelVolume,
        fuelSaving,
        unlock,
        lock,
        callAuthorized,
        callForbidden,
        callAuthorizedDuration,
        callForbiddenDuration,
        numberTripWithForbiddenCall,
        speedingDuration,
        speedingDistance,
        efficiencyBrake,
        efficiencyAcceleration,
        efficiencySpeedMaintain
    )
    return Timeline(this.period, allContext, roadContexts)
}