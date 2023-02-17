package com.drivequant.drivekit.timeline.ui.timeline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.CalendarField
import com.drivequant.drivekit.common.ui.extension.startingFrom
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.TimelinePeriod
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.driverdata.timeline.TimelineQueryListener
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKRawTimeline
import com.drivequant.drivekit.timeline.ui.*
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorViewModel
import com.drivequant.drivekit.timeline.ui.component.graph.GraphItem
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineGraphListener
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.TimelineGraphViewModel
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorItemListener
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
import com.drivequant.drivekit.timeline.ui.toTimelineDate
import java.util.*

internal class TimelineViewModel(application: Application) : AndroidViewModel(application) {

    val updateData = MutableLiveData<Any>()

    val scores: List<DKScoreType> = DriveKitDriverDataTimelineUI.scores.toMutableList()

    var currentPeriod: DKTimelinePeriod = DKTimelinePeriod.values().first()
        private set

    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()

    var selectedScore: DKScoreType = scores.first()
        private set

    var weekTimeline: DKRawTimeline? = null
        private set
    var monthTimeline: DKRawTimeline? = null
        private set

    var selectedDate: Date? = null
        private set

    val periodSelectorViewModel = PeriodSelectorViewModel()
    val roadContextViewModel = RoadContextViewModel()
    val dateSelectorViewModel = DateSelectorViewModel()
    val graphViewModel = TimelineGraphViewModel()

    init {
        periodSelectorViewModel.listener = object : PeriodSelectorItemListener {
            override fun onPeriodSelected(period: DKTimelinePeriod) {
                if (currentPeriod != period) {
                    currentPeriod = period

                    TimelineUtils.updateSelectedDateForNewPeriod(period, selectedDate, weekTimeline, monthTimeline)?.let {
                        this@TimelineViewModel.selectedDate = it
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
        DriveKitDriverData.getTimelines(
            DKTimelinePeriod.values().asList(),
            object : TimelineQueryListener {
                override fun onResponse(
                    timelineSyncStatus: TimelineSyncStatus,
                    timelines: List<DKRawTimeline>
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
            },
            SynchronizationType.CACHE
        )
        updateTimeline()
    }

    fun updateTimeline() {
        DriveKitDriverData.getTimelines(
            DKTimelinePeriod.values().asList(),
            object : TimelineQueryListener {
                override fun onResponse(
                    timelineSyncStatus: TimelineSyncStatus,
                    timelines: List<DKRawTimeline>
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
            val cleanedTimeline = timelineSource.cleanedTimeline(selectedScore, initialSelectedDateIndex)

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
                this.selectedDate = dates[selectedDateIndex]
                // Update view models
                periodSelectorViewModel.configure(this.currentPeriod)
                dateSelectorViewModel.configure(dates, selectedDateIndex, currentPeriod)
                roadContextViewModel.configure(cleanedTimeline, selectedScore, selectedDateIndex)
                graphViewModel.configure(getApplication(), cleanedTimeline, selectedDateIndex, GraphItem.Score(selectedScore), currentPeriod)
            } else {
                configureWithNoData()
            }
        } ?: run {
            configureWithNoData()
        }
        updateData.postValue(Any())
    }

    fun updateTimelineDateAndPeriod(period: DKTimelinePeriod, date: Date) {
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
            roadContextViewModel.configure(null, selectedScore, null)
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
