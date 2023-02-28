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
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.startingFrom
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.mysynthesis.component.scorecard.MySynthesisScoreCardViewModel
import java.util.*

internal class MySynthesisViewModel(application: Application) : AndroidViewModel(application) {

    private val scores = DriverDataUI.scores
    private val periods = listOf(DKPeriod.WEEK, DKPeriod.MONTH, DKPeriod.YEAR)
    val periodSelectorViewModel = DKPeriodSelectorViewModel()
    val scoreSelectorViewModel = DKScoreSelectorViewModel()
    val dateSelectorViewModel = DKDateSelectorViewModel()
    val scoreCardViewModel = MySynthesisScoreCardViewModel()
    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()
    val updateData = MutableLiveData<Any>()
    var selectedScore: DKScoreType
        private set
    private var selectedPeriod: DKPeriod = this.periods.last()
    private var selectedDate: Date? = null
    private var timelineByPeriod: Map<DKPeriod, DKDriverTimeline> = mapOf()

    init {
        this.selectedScore = this.scores.firstOrNull() ?: DKScoreType.SAFETY
        configureScoreSelector()
        configurePeriodSelector()
        configureDateSelector()
        DriveKitDriverData.getDriverTimelines(this.periods, SynchronizationType.CACHE) { status, timelines ->
            if (status == TimelineSyncStatus.CACHE_DATA_ONLY) {
                this.timelineByPeriod = timelines.associateBy { it.period }
                update()
            }
        }
        updateData()
    }

    fun updateData() {
        DriveKitDriverData.getDriverTimelines(this.periods, SynchronizationType.DEFAULT) { status, timelines ->
            if (status != TimelineSyncStatus.NO_TIMELINE_YET) {
                this.timelineByPeriod = timelines.associateBy { it.period }
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
            val dates = timelineSource.allContext.mapNotNull { allContextItem ->
                if (this.selectedScore == DKScoreType.DISTRACTION || this.selectedScore == DKScoreType.SPEEDING || allContextItem.date == this.selectedDate || (allContextItem.safety != null && allContextItem.ecoDriving != null)) {
                    allContextItem.date
                } else {
                    null
                }
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
                this.scoreCardViewModel.configure(this.selectedScore, this.selectedPeriod, timelineSource, this.selectedDate)
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
        this.periodSelectorViewModel.select(this.selectedPeriod)
        this.periodSelectorViewModel.onPeriodSelected = { period ->
            if (this.selectedPeriod != period) {
                val oldPeriod = this.selectedPeriod
                val selectedDate = this.selectedDate
                val sourceTimeline = getTimelineSource(period)
                if (selectedDate != null && sourceTimeline != null) {
                    this.selectedDate = updateSelectedDate(selectedDate, oldPeriod, period, sourceTimeline.allContext.map { it.date })
                }
                this.selectedPeriod = period
                update()
            }
        }
    }

    private fun updateSelectedDate(currentSelectedDate: Date, oldPeriod: DKPeriod, newPeriod: DKPeriod, newDates: List<Date>): Date {
        val compareDate: Date? = if (oldPeriod == newPeriod) {
            null
        } else if (newPeriod == DKPeriod.YEAR) {
            currentSelectedDate.startingFrom(CalendarField.YEAR)
        } else if (oldPeriod > newPeriod) {
            currentSelectedDate
        } else { // oldPeriod = WEEK, newPeriod = MONTH
            currentSelectedDate.startingFrom(CalendarField.MONTH)
        }
        return compareDate?.let { date ->  newDates.firstOrNull { it >= date } } ?: currentSelectedDate
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

    private fun getTimelineSource(period: DKPeriod = this.selectedPeriod) = this.timelineByPeriod[period]

    class MySynthesisViewModelFactory(private val application: Application) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MySynthesisViewModel(application) as T
        }
    }

}
