package com.drivequant.drivekit.timeline.ui

import com.drivequant.drivekit.databaseutils.entity.DKRawAllContextItem
import com.drivequant.drivekit.databaseutils.entity.DKRawRoadContextItem
import com.drivequant.drivekit.databaseutils.entity.DKRawTimeline
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.toTimelineRoadContext


internal fun DKRawTimeline.hasValidTripScored(selectedIndex: Int) =
    this.allContext.numberTripScored[selectedIndex, 0] > 0

internal fun DKRawTimeline.hasData(): Boolean {
    return this.allContext.numberTripTotal.isNotEmpty()
}

internal fun DKRawTimeline.distanceByRoadContext(selectedIndex: Int): Map<TimelineRoadContext, Double> {
    val distanceByContext = mutableMapOf<TimelineRoadContext, Double>()
    if (this.hasValidTripScored(selectedIndex)) {
        this.roadContexts.forEach {
            val distance = it.distance[selectedIndex, 0.0]
            if (distance > 0) {
                distanceByContext[it.type.toTimelineRoadContext()] = distance
            }
        }
    }
    return distanceByContext
}

internal fun DKRawTimeline.totalDistanceForAllContexts(selectedIndex: Int) : Double {
    var totalDistanceForAllContexts = 0.0
    if (this.hasValidTripScored(selectedIndex)) {
        totalDistanceForAllContexts = this.allContext.distance[selectedIndex, 0.0]
    }
    return totalDistanceForAllContexts
}

internal fun DKRawTimeline.cleanedTimeline(selectedDateIndex: Int?): DKRawTimeline {
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
    val tripCategory = mutableListOf<List<Int>>()
    val tripCategoryDistance = mutableListOf<List<Double>>()
    val weather = mutableListOf<List<Int>>()
    val weatherDistance = mutableListOf<List<Double>>()
    val dayDistance = mutableListOf<Double>()
    val weekDayDistance = mutableListOf<Double>()

    val allContextItem = this.allContext

    val canInsertAtIndex: (Int) -> Boolean = { pos ->
        this.allContext.numberTripScored[pos] > 0 || selectedDateIndex == pos
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
            allContextItem.tripCategory.addValueIfNotEmpty(index, tripCategory)
            allContextItem.tripCategoryDistance.addValueIfNotEmpty(index, tripCategoryDistance)
            allContextItem.weather.addValueIfNotEmpty(index, weather)
            allContextItem.weatherDistance.addValueIfNotEmpty(index, weatherDistance)
            allContextItem.dayDistance.addValueIfNotEmpty(index, dayDistance)
            allContextItem.weekDayDistance.addValueIfNotEmpty(index, weekDayDistance)
        }
    }

    val roadContexts = mutableListOf<DKRawRoadContextItem>()
    this.roadContexts.forEachIndexed { _, roadContextItem ->
        val newDate = mutableListOf<String>()
        val newNumberTripTotal = mutableListOf<Int>()
        val newNumberTripScored = mutableListOf<Int>()
        val newDistance = mutableListOf<Double>()
        val newDuration = mutableListOf<Int>()
        val newEfficiency = mutableListOf<Double>()
        val newSafety = mutableListOf<Double>()
        val newAcceleration = mutableListOf<Int>()
        val newBraking = mutableListOf<Int>()
        val newAdherence = mutableListOf<Int>()
        val newCo2Mass = mutableListOf<Double>()
        val newFuelVolume = mutableListOf<Double>()
        val newFuelSaving = mutableListOf<Double>()
        val newEfficiencyAcceleration = mutableListOf<Double>()
        val newEfficiencyBrake = mutableListOf<Double>()
        val newEfficiencySpeedMaintain = mutableListOf<Double>()

        this.allContext.date.forEachIndexed { index, _ ->
            val currentDate = roadContextItem.date.getSafe(index)
            if (currentDate != null && canInsertAtIndex(index)) {
                newDate.add(currentDate)
                roadContextItem.numberTripTotal.addValueIfNotEmpty(index, newNumberTripTotal)
                roadContextItem.numberTripScored.addValueIfNotEmpty(index, newNumberTripScored)
                roadContextItem.distance.addValueIfNotEmpty(index, newDistance)
                roadContextItem.duration.addValueIfNotEmpty(index, newDuration)
                roadContextItem.efficiency.addValueIfNotEmpty(index, newEfficiency)
                roadContextItem.safety.addValueIfNotEmpty(index, newSafety)
                roadContextItem.acceleration.addValueIfNotEmpty(index, newAcceleration)
                roadContextItem.braking.addValueIfNotEmpty(index, newBraking)
                roadContextItem.adherence.addValueIfNotEmpty(index, newAdherence)
                roadContextItem.co2Mass.addValueIfNotEmpty(index, newCo2Mass)
                roadContextItem.fuelVolume.addValueIfNotEmpty(index, newFuelVolume)
                roadContextItem.fuelSaving.addValueIfNotEmpty(index, newFuelSaving)
                roadContextItem.efficiencyAcceleration.addValueIfNotEmpty(index, newEfficiencyAcceleration)
                roadContextItem.efficiencyBrake.addValueIfNotEmpty(index, newEfficiencyBrake)
                roadContextItem.efficiencySpeedMaintain.addValueIfNotEmpty(index, newEfficiencySpeedMaintain)
            }
        }

        val newRoadContext = DKRawRoadContextItem(
            roadContextItem.type,
            newDate,
            newNumberTripTotal,
            newNumberTripScored,
            newDistance,
            newDuration,
            newEfficiency,
            newSafety,
            newAcceleration,
            newBraking,
            newAdherence,
            newCo2Mass,
            newFuelVolume,
            newFuelSaving,
            newEfficiencyAcceleration,
            newEfficiencyBrake,
            newEfficiencySpeedMaintain
        )
        roadContexts.add(newRoadContext)
    }

    val allContext = DKRawAllContextItem(
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
        efficiencySpeedMaintain,
        tripCategory,
        tripCategoryDistance,
        weather,
        weatherDistance,
        dayDistance,
        weekDayDistance
    )
    return DKRawTimeline(this.period, allContext, roadContexts)
}
