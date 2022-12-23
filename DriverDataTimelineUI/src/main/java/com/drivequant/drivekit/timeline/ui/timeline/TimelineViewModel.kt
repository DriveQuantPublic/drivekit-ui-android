package com.drivequant.drivekit.timeline.ui.timeline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.CalendarField
import com.drivequant.drivekit.common.ui.extension.removeTime
import com.drivequant.drivekit.common.ui.extension.startingFrom
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.AllContextItem
import com.drivequant.drivekit.databaseutils.entity.RoadContextItem
import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.databaseutils.entity.TimelinePeriod
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.driverdata.timeline.TimelineQueryListener
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.timeline.ui.DriveKitDriverDataTimelineUI
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorViewModel
import com.drivequant.drivekit.timeline.ui.component.graph.GraphItem
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineGraphListener
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.TimelineGraphViewModel
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorItemListener
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.toTimelineRoadContext
import com.drivequant.drivekit.timeline.ui.toTimelineDate
import java.util.*

internal class TimelineViewModel(application: Application) : AndroidViewModel(application) {

    var updateData = MutableLiveData<Any>()

    var scores: List<DKScoreType> = DriveKitDriverDataTimelineUI.scores.toMutableList()

    var timelinePeriodTypes = DKTimelinePeriod.values().toList()
    private var currentPeriod: DKTimelinePeriod = timelinePeriodTypes.first()

    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()

    private var selectedScore: DKScoreType = scores.first()
        set(value) {
            field = value
            update()
        }

    var periodSelectorViewModel = PeriodSelectorViewModel()
    var roadContextViewModel = RoadContextViewModel()
    var dateSelectorViewModel = DateSelectorViewModel()
    var graphViewModel = TimelineGraphViewModel()

    private var weekTimeline: Timeline? = null
    private var monthTimeline: Timeline? = null

    private var selectedDate: Date? = null

    private val hasData: Boolean
        get() {
            return getTimelineSource()?.allContext?.numberTripTotal?.isNotEmpty() ?: run { false }
        }

    init {
        periodSelectorViewModel.listener = object : PeriodSelectorItemListener {
            override fun onPeriodSelected(period: DKTimelinePeriod) {
                if (currentPeriod != period) {
                    currentPeriod = period

                    // get nearest date
                    getTimelineSource()?.let { timeline ->
                        selectedDate?.let { selectedDate ->
                            val compareDate: Date = if (period == DKTimelinePeriod.WEEK) {
                                // month to week
                                selectedDate
                            } else {
                                // month to week
                                selectedDate.startingFrom(CalendarField.MONTH).removeTime()
                            }
                            compareDate.let {
                                val dates = timeline.allContext.date.map {
                                    it.toTimelineDate()!!
                                }
                                this@TimelineViewModel.selectedDate = dates.first { date ->
                                    date >= compareDate
                                }
                            }
                        }
                    }
                    update()
                }
            }
        }
        graphViewModel.listener = object : TimelineGraphListener {
            override fun onSelectDate(date: Date) {
                this@TimelineViewModel.selectedDate = date
                update()
            }
        }
        DriveKitDriverData.getTimelines(DKTimelinePeriod.values().asList(), object : TimelineQueryListener {
            override fun onResponse(
                timelineSyncStatus: TimelineSyncStatus,
                timelines: List<Timeline>
            ) {
                if (timelineSyncStatus == TimelineSyncStatus.CACHE_DATA_ONLY) {
                    timelines.forEach {
                        when (it.period) {
                            TimelinePeriod.WEEK -> weekTimeline = it
                            TimelinePeriod.MONTH -> monthTimeline = it
                        }
                    }
                    update()
                }
            }
        }, SynchronizationType.CACHE)
        updateTimeline()
    }

    fun updateTimeline() {
        DriveKitDriverData.getTimelines(DKTimelinePeriod.values().asList(), object : TimelineQueryListener {
            override fun onResponse(
                timelineSyncStatus: TimelineSyncStatus,
                timelines: List<Timeline>
            ) {
                if (timelineSyncStatus != TimelineSyncStatus.NO_TIMELINE_YET) {
                    timelines.forEach {
                        when (it.period) {
                            TimelinePeriod.WEEK -> weekTimeline = it
                            TimelinePeriod.MONTH -> monthTimeline = it
                        }
                    }
                    update(resettingSelectedDate = true)
                }
                syncStatus.postValue(timelineSyncStatus)
            }
        })
    }

    private fun update(resettingSelectedDate: Boolean = false) {
        getTimelineSource()?.let { timelineSource ->
            if (resettingSelectedDate) {
                selectedDate = null
            }

            // Clean timeline to remove, if needed, values where there are only unscored trips
            val sourceDates = timelineSource.allContext.date.map {
                it.toTimelineDate()!!
            }
            val initialSelectedDateIndex = selectedDate?.let { sourceDates.indexOf(it) }
            val cleanedTimeline = cleanTimeline(timelineSource, selectedScore, initialSelectedDateIndex)

            // Compute selected index
            val dates = cleanedTimeline.allContext.date.map {
                it.toTimelineDate()!!
            }
            val selectedDateIndex = if (selectedDate != null) {
                dates.indexOf(selectedDate)
            } else if (dates.isNotEmpty()) {
                dates.count() - 1
            } else {
                null
            }
            if (selectedDateIndex != null && selectedDateIndex >= 0) {
                val distanceByContext = mutableMapOf<TimelineRoadContext, Double>()

                val scoredTripsCount = cleanedTimeline.allContext.numberTripScored[selectedDateIndex]
                if (selectedScore == DKScoreType.DISTRACTION || selectedScore == DKScoreType.SPEEDING || scoredTripsCount > 0) {
                    cleanedTimeline.roadContexts.forEach {
                        val distance = it.distance[selectedDateIndex]
                        if (distance > 0) {
                            distanceByContext[it.type.toTimelineRoadContext()] = distance
                        }
                    }
                }

                // Update view models
                dateSelectorViewModel.configure(dates, selectedDateIndex, currentPeriod)
                roadContextViewModel.configure(cleanedTimeline, selectedScore, selectedDateIndex, distanceByContext, hasData)
                graphViewModel.configure(getApplication(), cleanedTimeline, selectedDateIndex, GraphItem.Score(selectedScore), currentPeriod)
            } else {
                configureWithNoData()
            }
        } ?: run {
            configureWithNoData()
        }
        updateData.postValue(Any())
    }

    fun updateTimelinePeriod(period: DKTimelinePeriod) {
        if (currentPeriod != period) {
            currentPeriod = period
            update()
        }
    }

    fun updateTimelineDate(date: Date) {
        if (selectedDate != date) {
            selectedDate = date
            update()
        }
    }

    fun updateTimelineScore(position: Int) {
        selectedScore = scores[position]
        update()
    }

    private fun getTimelineSource() = when (currentPeriod) {
        DKTimelinePeriod.WEEK -> weekTimeline
        DKTimelinePeriod.MONTH -> monthTimeline
    }

    private fun configureWithNoData() {
        when (currentPeriod) {
            DKTimelinePeriod.WEEK -> Date().startingFrom(CalendarField.WEEK)
            DKTimelinePeriod.MONTH -> Date().startingFrom(CalendarField.MONTH)
        }.let { startDate ->
            dateSelectorViewModel.configure(listOf(startDate), 0, currentPeriod)
            roadContextViewModel.configure(null, selectedScore, null, mapOf(), false)
        }
    }

    private fun cleanTimeline(
        timeline: Timeline,
        score: DKScoreType,
        selectedDateIndex: Int?
    ): Timeline {
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

        val allContextItem = timeline.allContext

        val canInsertAtIndex: (Int) -> Boolean = { pos ->
            timeline.allContext.numberTripScored[pos] > 0
                    || score == DKScoreType.DISTRACTION
                    || score == DKScoreType.SPEEDING
                    || selectedDateIndex == pos
        }

        allContextItem.date.forEachIndexed { index, _ ->
            if (canInsertAtIndex(index)) {
                date.add(allContextItem.date[index])
                numberTripTotal.add(allContextItem.numberTripTotal[index])
                numberTripScored.add(allContextItem.numberTripScored[index])
                distance.add(allContextItem.distance[index])
                duration.add(allContextItem.duration[index])
                efficiency.add(allContextItem.efficiency[index])
                safety.add(allContextItem.safety[index])
                acceleration.add(allContextItem.acceleration[index])
                braking.add(allContextItem.braking[index])
                adherence.add(allContextItem.adherence[index])
                phoneDistraction.add(allContextItem.phoneDistraction[index])
                speeding.add(allContextItem.speeding[index])
                co2Mass.add(allContextItem.co2Mass[index])
                fuelVolume.add(allContextItem.fuelVolume[index])
                fuelSaving.add(allContextItem.fuelSaving[index])
                unlock.add(allContextItem.unlock[index])
                lock.add(allContextItem.lock[index])
                callAuthorized.add(allContextItem.callAuthorized[index])
                callForbidden.add(allContextItem.callForbidden[index])
                callForbiddenDuration.add(allContextItem.callForbiddenDuration[index])
                callAuthorizedDuration.add(allContextItem.callAuthorizedDuration[index])
                if (allContextItem.numberTripWithForbiddenCall.isNotEmpty()) { // Some old trips may have no these distractions values
                    numberTripWithForbiddenCall.add(allContextItem.numberTripWithForbiddenCall[index])
                    speedingDuration.add(allContextItem.speedingDuration[index])
                    speedingDistance.add(allContextItem.speedingDistance[index])
                    efficiencyBrake.add(allContextItem.efficiencyBrake[index])
                    efficiencyAcceleration.add(allContextItem.efficiencyAcceleration[index])
                    efficiencySpeedMaintain.add(allContextItem.efficiencySpeedMaintain[index])
                }
            }
        }

        val roadContexts = mutableListOf<RoadContextItem>()
        timeline.roadContexts.forEachIndexed { _, roadContextItem ->
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

            timeline.allContext.date.forEachIndexed { index, _ ->
                if (canInsertAtIndex(index)) {
                    date.add(roadContextItem.date[index])
                    numberTripTotal.add(roadContextItem.numberTripTotal[index])
                    numberTripScored.add(roadContextItem.numberTripScored[index])
                    distance.add(roadContextItem.distance[index])
                    duration.add(roadContextItem.duration[index])
                    efficiency.add(roadContextItem.efficiency[index])
                    safety.add(roadContextItem.safety[index])
                    acceleration.add(roadContextItem.acceleration[index])
                    braking.add(roadContextItem.braking[index])
                    adherence.add(roadContextItem.adherence[index])
                    co2Mass.add(roadContextItem.co2Mass[index])
                    fuelVolume.add(roadContextItem.fuelVolume[index])
                    fuelSaving.add(roadContextItem.fuelSaving[index])
                    if (roadContextItem.efficiencyAcceleration.isNotEmpty()) {
                        efficiencyAcceleration.add(roadContextItem.efficiencyAcceleration[index])
                        efficiencyBrake.add(roadContextItem.efficiencyBrake[index])
                        efficiencySpeedMaintain.add(roadContextItem.efficiencySpeedMaintain[index])
                    }
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
        return Timeline(timeline.period, allContext, roadContexts)
    }

    @Suppress("UNCHECKED_CAST")
    class TimelineViewModelFactory(private val application: Application) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimelineViewModel(application) as T
        }
    }
}
