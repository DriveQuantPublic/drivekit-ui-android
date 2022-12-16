package com.drivequant.drivekit.timeline.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.extension.CalendarField
import com.drivequant.drivekit.common.ui.extension.removeTime
import com.drivequant.drivekit.common.ui.extension.startingFrom
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.driverdata.timeline.TimelineQueryListener
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.timeline.ui.DKTimelineScoreType
import com.drivequant.drivekit.timeline.ui.DriveKitDriverDataTimelineUI
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorViewModel
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorItemListener
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.toTimelineRoadContext
import com.drivequant.drivekit.timeline.ui.toTimelineDate
import java.util.*

internal class TimelineViewModel : ViewModel() {

    var updateData = MutableLiveData<Any>()

    var scores: List<DKTimelineScoreType> = DriveKitDriverDataTimelineUI.scores.toMutableList()

    var timelinePeriodTypes = DKTimelinePeriod.values().toList()
    private var currentPeriod: DKTimelinePeriod = timelinePeriodTypes.first()

    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()

    private var selectedScore: DKTimelineScoreType = scores.first()
        set(value) {
            field = value
            update()
        }

    var periodSelectorViewModel = PeriodSelectorViewModel()
    var roadContextViewModel = RoadContextViewModel()
    var dateSelectorViewModel = DateSelectorViewModel()

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

                    // get neariest date
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
                                    date > compareDate
                                }
                            }
                        }
                    }

                    update()
                }
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
        if (resettingSelectedDate) {
           selectedDate = null
        }
        getTimelineSource()?.let { timelineSource ->
            val dates = timelineSource.allContext.date.map {
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

                val totalTripNumber = getTimelineSource()?.allContext?.numberTripTotal?.get(selectedDateIndex) ?: 0
                if (selectedScore == DKTimelineScoreType.DISTRACTION || selectedScore == DKTimelineScoreType.SPEEDING || totalTripNumber > 0) {
                    timelineSource.roadContexts.forEach {
                        val distance = it.distance[selectedDateIndex]
                        if (distance > 0) {
                            distanceByContext[it.type.toTimelineRoadContext()] = distance
                        }
                    }
                }
               
                roadContextViewModel.configure(selectedScore, distanceByContext as Map<TimelineRoadContext, Double>, hasData)
                dateSelectorViewModel.configure(dates, selectedDateIndex, currentPeriod)
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
            roadContextViewModel.configure(selectedScore, mapOf(), false)
        }
    }
}
