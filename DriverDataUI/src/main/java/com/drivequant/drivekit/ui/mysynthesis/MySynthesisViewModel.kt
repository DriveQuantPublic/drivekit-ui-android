package com.drivequant.drivekit.ui.mysynthesis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorViewModel
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorViewModel
import com.drivequant.drivekit.common.ui.component.scoreselector.DKScoreSelectorViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.common.DKPeriod
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.startingFrom
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.TimelinePeriod
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.ui.DriverDataUI
import java.util.Date

internal class MySynthesisViewModel(application: Application) : AndroidViewModel(application) {

    private val scores = DriverDataUI.scores
    private val periods = listOf(DKPeriod.WEEK, DKPeriod.MONTH, DKPeriod.YEAR)
    val periodSelectorViewModel = DKPeriodSelectorViewModel()
    val scoreSelectorViewModel = DKScoreSelectorViewModel()
    val dateSelectorViewModel = DKDateSelectorViewModel()
    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()
    val updateData = MutableLiveData<Any>()
    private var selectedScore: DKScoreType
    private var selectedPeriod: DKPeriod = this.periods.first()
    private var selectedDate: Date? = null
    private var weekTimeline: DKDriverTimeline? = null
    private var monthTimeline: DKDriverTimeline? = null
    private var yearTimeline: DKDriverTimeline? = null

    init {
        this.selectedScore = this.scores.firstOrNull() ?: DKScoreType.SAFETY
        configureScoreSelector()
        configurePeriodSelector()
        configureDateSelector()
        DriveKitDriverData.getDriverTimelines(this.periods, SynchronizationType.CACHE) { status, timelines ->
            if (status == TimelineSyncStatus.CACHE_DATA_ONLY) {
                timelines.forEach {
                    when (it.period) {
                        TimelinePeriod.WEEK -> weekTimeline = it
                        TimelinePeriod.MONTH -> monthTimeline = it
                        TimelinePeriod.YEAR -> yearTimeline = it
                    }
                }
                update()
            }
        }
        updateData()
    }

    fun updateData() {
        DriveKitDriverData.getDriverTimelines(this.periods, SynchronizationType.DEFAULT) { status, timelines ->
            if (status != TimelineSyncStatus.NO_TIMELINE_YET) {
                timelines.forEach {
                    when (it.period) {
                        TimelinePeriod.WEEK -> weekTimeline = it
                        TimelinePeriod.MONTH -> monthTimeline = it
                        TimelinePeriod.YEAR -> yearTimeline = it
                    }
                }
                update(true)
            }
            syncStatus.postValue(status)
        }
    }

    private fun update(resettingSelectedDate: Boolean = false) {
        getTimelineSource()?.let { timelineSource ->
            if (resettingSelectedDate) {
                selectedDate = null
            }
            val dates = timelineSource.allContext.map { allContextItem ->
                allContextItem.date
            }
            if (dates.isNotEmpty()) {
                val selectedDateIndex: Int = this.selectedDate?.let {
                    val index = dates.indexOf(it)
                    if (index < 0) {
                        null
                    } else {
                        index
                    }
                } ?: (dates.size - 1)
                this.selectedDate = dates[selectedDateIndex]
                this.dateSelectorViewModel.configure(dates, selectedDateIndex, this.selectedPeriod)
            }
        } ?: run {
            configureWithNoData()
        }
        this.updateData.postValue(Any())
    }

    private fun configureWithNoData() {
        when (this.selectedPeriod) {
            DKPeriod.WEEK -> Date().startingFrom(CalendarField.WEEK)
            DKPeriod.MONTH -> Date().startingFrom(CalendarField.MONTH)
            DKPeriod.YEAR -> Date().startingFrom(CalendarField.YEAR)
        }.let { startDate ->
            dateSelectorViewModel.configure(listOf(startDate), 0, this.selectedPeriod)
        }
    }

    private fun configureScoreSelector() {
        this.scoreSelectorViewModel.configure(this.scores) { score ->
            if (this.selectedScore != score) {
                this.selectedScore = score
                update()
            }
        }
    }

    private fun configurePeriodSelector() {
        this.periodSelectorViewModel.configure(periods)
        this.periodSelectorViewModel.onPeriodSelected = { period ->
            if (this.selectedPeriod != period) {
                this.selectedPeriod = period
                update()
            }
        }
    }

    private fun configureDateSelector() {
        this.dateSelectorViewModel.onDateSelected = { date ->
            updateSelectedDate(date)
        }
    }

    private fun updateSelectedDate(date: Date) {
        if (this.selectedDate != date) {
            this.selectedDate = date
            update()
        }
    }

    private fun getTimelineSource() = when (this.selectedPeriod) {
        DKPeriod.WEEK -> this.weekTimeline
        DKPeriod.MONTH -> this.monthTimeline
        DKPeriod.YEAR -> this.yearTimeline
    }

    class MySynthesisViewModelFactory(private val application: Application) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MySynthesisViewModel(application) as T
        }
    }

}
