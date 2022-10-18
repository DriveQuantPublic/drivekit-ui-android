package com.drivequant.drivekit.timeline.ui.timeline

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.driverdata.timeline.TimelineQueryListener
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.timeline.ui.DKTimelineScore
import com.drivequant.drivekit.timeline.ui.DriverDataTimelineUI

internal class TimelineViewModel : ViewModel() {

    private var selectedTimelineScoreType: DKTimelineScore
    private var selectedTimelinePeriod: DKTimelinePeriod

    var timelineScoreTypes = mutableListOf<DKTimelineScore>()
    var timelinePeriodTypes = mutableListOf<DKTimelinePeriod>()

    val timelineDataLiveData: MutableLiveData<TimelineData> = MutableLiveData()
    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData(TimelineSyncStatus.NO_ERROR)

    init {
        timelineScoreTypes.addAll(DriverDataTimelineUI.scoresType)
        timelinePeriodTypes.addAll(DKTimelinePeriod.values().toList())

        selectedTimelineScoreType = timelineScoreTypes.first()
        selectedTimelinePeriod = timelinePeriodTypes.first()
    }

    fun fetchTimeline() {
        DriveKitDriverData.getTimelines(
            periods = DKTimelinePeriod.values().toList(),
            listener = object : TimelineQueryListener {
                override fun onResponse(
                    timelineSyncStatus: TimelineSyncStatus,
                    timelines: List<Timeline>
                ) {
                    syncStatus.postValue(timelineSyncStatus)
                }
            },
            synchronizationType = SynchronizationType.DEFAULT
        )
    }

    private fun updateTimeline() {
        val periods = listOf(selectedTimelinePeriod)
        DriveKitDriverData.getTimelines(
            periods = periods,
            listener = object : TimelineQueryListener {
                override fun onResponse(
                    timelineSyncStatus: TimelineSyncStatus,
                    timelines: List<Timeline>
                ) {
                    val timelinesData = timelines.map { it.toTimelineData() }
                    if (timelinesData.isNotEmpty()) {
                        timelineDataLiveData.postValue(
                            timelinesData[0]
                        )
                    }
                }
            },
            synchronizationType = SynchronizationType.CACHE
        )
    }

    fun updateTimelinePeriod(period: DKTimelinePeriod) {
        selectedTimelinePeriod = period
        Log.e("TEST", selectedTimelinePeriod.name)
        updateTimeline()
    }

    fun updateTimelineScore(position: Int) {
        selectedTimelineScoreType = timelineScoreTypes[position]
        Log.e("TEST", selectedTimelineScoreType.name)
        updateTimeline()
    }
}

data class RoadContextItemData(
    val type: RoadContext,
    val date: List<String>,
    val numberTripTotal: List<Int>,
    val numberTripScored: List<Int>,
    val distance: List<Double>,
    val duration: List<Int>,
    val efficiency: List<Double>,
    val safety: List<Double>,
    val acceleration: List<Int>,
    val braking: List<Int>,
    val adherence: List<Int>,
    val co2Mass: List<Double>,
    val fuelVolume: List<Double>,
    val efficiencyAcceleration: List<Double>,
    val efficiencyBrake: List<Double>,
    val efficiencySpeedMaintain: List<Double>
)

data class AllContextItemData(
    val date: List<String>,
    val numberTripScored: List<Int>,
    val numberTripTotal: List<Int>,
    val distance: List<Double>,
    val duration: List<Int>,
    val efficiency: List<Double>,
    val safety: List<Double>,
    val acceleration: List<Int>,
    val braking: List<Int>,
    val adherence: List<Int>,
    val phoneDistraction: List<Double>,
    val speeding: List<Double>,
    val co2Mass: List<Double>,
    val fuelVolume: List<Double>,
    val unlock: List<Int>,
    val lock: List<Int>,
    val callAuthorized: List<Int>,
    val callForbidden: List<Int>,
    val callAuthorizedDuration: List<Int>,
    val callForbiddenDuration: List<Int>,
    val numberTripWithForbiddenCall: List<Int>,
    val speedingDuration: List<Int>,
    val speedingDistance: List<Double>,
    val efficiencyBrake: List<Double>,
    val efficiencyAcceleration: List<Double>,
    val efficiencySpeedMaintain: List<Double>
)

data class TimelineData(
    val period: DKTimelinePeriod,
    val allContext: AllContextItemData,
    val roadContexts: List<RoadContextItemData>
)

fun RoadContextItem.toRoadContextItemData() = RoadContextItemData(
    type,
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
    efficiencyAcceleration,
    efficiencyBrake,
    efficiencySpeedMaintain
)

fun AllContextItem.toAllContextItemData() = AllContextItemData(
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

fun Timeline.toTimelineData() = TimelineData(
    period = period.toDKTimelinePeriod(),
    allContext = allContext.toAllContextItemData(),
    roadContexts = roadContexts.map { it.toRoadContextItemData() }
)

fun TimelinePeriod.toDKTimelinePeriod() = when (this) {
    TimelinePeriod.WEEK -> DKTimelinePeriod.WEEK
    TimelinePeriod.MONTH -> DKTimelinePeriod.MONTH
}

//TODO (Replace with title resId)
fun DKTimelinePeriod.getTitleResId() = when(this) {
    DKTimelinePeriod.WEEK -> "Par semaine"
    DKTimelinePeriod.MONTH -> "Par mois"
}