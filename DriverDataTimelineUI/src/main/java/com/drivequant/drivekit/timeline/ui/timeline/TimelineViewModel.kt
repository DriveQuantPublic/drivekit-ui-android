package com.drivequant.drivekit.timeline.ui.timeline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorViewModel
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorViewModel
import com.drivequant.drivekit.common.ui.component.scoreselector.DKScoreSelectorViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.startingFrom
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.timeline.ui.TimelineUtils
import com.drivequant.drivekit.timeline.ui.component.graph.GraphItem
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineGraphListener
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.TimelineGraphViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
import java.util.Date

internal class TimelineViewModel(application: Application) : AndroidViewModel(application) {

    private val periods = listOf(DKPeriod.WEEK, DKPeriod.MONTH)
    private val scores: List<DKScoreType> = DriveKitUI.scores

    val updateData = MutableLiveData<Any>()

    var currentPeriod: DKPeriod = this.periods.first()
        private set

    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()

    var selectedScore: DKScoreType = scores.first()
        private set

    private var timelineByPeriod: MutableMap<DKPeriod, DKDriverTimeline> = mutableMapOf()

    var selectedDate: Date? = null
        private set

    val scoreSelectorViewModel = DKScoreSelectorViewModel()
    val periodSelectorViewModel = DKPeriodSelectorViewModel()
    val roadContextViewModel = RoadContextViewModel()
    val dateSelectorViewModel = DKDateSelectorViewModel()
    val graphViewModel = TimelineGraphViewModel()

    init {
        scoreSelectorViewModel.configure(this.scores) { score ->
            selectedScore = score
            update()
        }
        periodSelectorViewModel.configure(periods)
        periodSelectorViewModel.onPeriodSelected = { oldPeriod, newPeriod ->
            if (currentPeriod != newPeriod) {
                currentPeriod = newPeriod

                getTimelineSource()?.let { timeline ->
                    TimelineUtils.updateSelectedDate(oldPeriod, this.selectedDate, timeline)?.let { newDate ->
                        this.selectedDate = newDate
                    }
                }
                update()
            }
        }
        graphViewModel.listener = object : TimelineGraphListener {
            override fun onSelectDate(date: Date) {
                this@TimelineViewModel.selectedDate = date
                update()
            }
        }
        DriveKitDriverData.getDriverTimelines(this.periods, synchronizationType = SynchronizationType.CACHE) { timelineSyncStatus, timelines ->
            if (timelineSyncStatus == TimelineSyncStatus.CACHE_DATA_ONLY) {
                timelines.forEach {
                    timelineByPeriod[it.period] = it
                }
                update()
            }
        }
        updateTimeline()
    }

    fun updateTimeline() {
        DriveKitDriverData.getDriverTimelines(this.periods) { timelineSyncStatus, timelines ->
            if (timelineSyncStatus != TimelineSyncStatus.NO_TIMELINE_YET) {
                timelines.forEach {
                    timelineByPeriod[it.period] = it
                }
                update(resettingSelectedDate = true)
            }
            syncStatus.postValue(timelineSyncStatus)
        }
    }

    private fun update(resettingSelectedDate: Boolean = false) {
        getTimelineSource()?.let { timeline ->
            if (resettingSelectedDate) {
                selectedDate = null
            }

            // Update view models
            var selectedAllContextItem: DKDriverTimeline.DKAllContextItem? = null
            var selectedDateIndex: Int? = null

            if (this.selectedDate != null) {
                val matchingAllContextItemIndex = timeline.allContext.indexOfFirst { it.date == this.selectedDate }
                if (matchingAllContextItemIndex != -1) {
                    selectedAllContextItem = timeline.allContext[matchingAllContextItemIndex]
                    selectedDateIndex = matchingAllContextItemIndex
                }
            } else {
                selectedAllContextItem = timeline.allContext.last()
                selectedDateIndex = if (timeline.allContext.isEmpty()) null else timeline.allContext.lastIndex
            }

            if (selectedAllContextItem != null && selectedDateIndex != null) {
                this.selectedDate = selectedAllContextItem.date
                val dates = timeline.allContext.map { it.date }
                periodSelectorViewModel.select(this.currentPeriod)
                dateSelectorViewModel.configure(dates, selectedDateIndex, currentPeriod)
                roadContextViewModel.configure(timeline, this.selectedDate)
                graphViewModel.configure(getApplication(), timeline, dates, selectedDateIndex, GraphItem.Score(selectedScore), currentPeriod)
            } else {
                configureWithNoData()
            }
        } ?: run {
            configureWithNoData()
        }
        updateData.postValue(Any())
    }

    fun updateTimelineDateAndPeriod(period: DKPeriod, date: Date) {
        var shouldUpdate = false
        if (currentPeriod != period) {
            shouldUpdate = true
            currentPeriod = period
        }
        if (selectedDate != date) {
            shouldUpdate = true
            selectedDate = date
        }
        if (shouldUpdate) {
            update()
        }
    }

    fun updateTimelinePeriod(period: DKPeriod) {
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

    private fun getTimelineSource() = timelineByPeriod[currentPeriod]

    private fun configureWithNoData() {
        when (currentPeriod) {
            DKPeriod.WEEK -> Date().startingFrom(CalendarField.WEEK)
            DKPeriod.MONTH -> Date().startingFrom(CalendarField.MONTH)
            DKPeriod.YEAR -> Date().startingFrom(CalendarField.YEAR)
        }.let { startDate ->
            dateSelectorViewModel.configure(listOf(startDate), 0, currentPeriod)
            roadContextViewModel.configure(null, null)
            graphViewModel.showEmptyGraph(GraphItem.Score(this.selectedScore), this.currentPeriod)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class TimelineViewModelFactory(private val application: Application) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimelineViewModel(application) as T
        }
    }
}
