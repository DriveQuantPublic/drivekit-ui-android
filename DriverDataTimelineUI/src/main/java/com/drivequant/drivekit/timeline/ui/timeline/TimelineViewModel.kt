package com.drivequant.drivekit.timeline.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.driverdata.timeline.TimelineQueryListener
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.timeline.ui.DKTimelineScoreType
import com.drivequant.drivekit.timeline.ui.DriveKitDriverDataTimelineUI
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.toTimelineRoadContext

internal class TimelineViewModel : ViewModel() {

    var updateData = MutableLiveData<Any>()

    var scores: List<DKTimelineScoreType> = DriveKitDriverDataTimelineUI.scores.toMutableList()

    var timelinePeriodTypes = DKTimelinePeriod.values().toList()
    private var currentPeriod: DKTimelinePeriod = timelinePeriodTypes.first()

    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()

    private var selectedScore: DKTimelineScoreType = scores.first()
        set(value) {
            field = value
            updateTimeline()
        }

    var roadContextViewModel = RoadContextViewModel()
    var dateSelectorViewModel = DateSelectorViewModel()

    private var weekTimeline: Timeline? = null
    private var monthTimeline: Timeline? = null

    private var selectedDate: String? = null

    val hasData: Boolean
        get() {
            return getTimelineSource()?.allContext?.numberTripTotal?.isNotEmpty() ?: run { false }
        }

    init {
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
            val dates = timelineSource.allContext.date
            val selectedDateIndex = if (selectedDate != null) {
                dates.indexOf(selectedDate)
            } else if (dates.isNotEmpty()) {
                dates.count() - 1
            } else {
                null
            }
            if (selectedDateIndex != null) {
                val distanceByContext = mutableMapOf<TimelineRoadContext, Double>()

                val totalTripNumber = getTimelineSource()?.allContext?.numberTripTotal?.get(selectedDateIndex) ?: 0
                if (selectedScore == DKTimelineScoreType.DISTRACTION || selectedScore == DKTimelineScoreType.SPEEDING || totalTripNumber > 0) {
                    timelineSource.roadContexts.forEach {
                        val distance = it.distance[selectedDateIndex]
                        distanceByContext[it.type.toTimelineRoadContext()] = distance
                    }
                }
               
                roadContextViewModel.configure(distanceByContext as Map<TimelineRoadContext, Double>, hasData)
                dateSelectorViewModel.configure(dates, selectedDateIndex, currentPeriod)
            }
        }
        updateData.postValue(Any())
    }

    fun updateTimelinePeriod(period: DKTimelinePeriod) {
        if (currentPeriod != period) {
            currentPeriod = period
            update(resettingSelectedDate = true)
        }
    }

    fun updateTimelineDate(date: String) {
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
}

//TODO Move in TimelinePeriodViewModel
fun DKTimelinePeriod.getTitleResId() = when(this) {
    DKTimelinePeriod.WEEK -> "dk_timeline_per_week"
    DKTimelinePeriod.MONTH -> "dk_timeline_per_month"
}

