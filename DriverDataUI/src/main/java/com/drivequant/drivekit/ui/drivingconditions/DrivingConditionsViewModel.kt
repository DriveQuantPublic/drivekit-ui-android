package com.drivequant.drivekit.ui.drivingconditions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorViewModel
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.startingFrom
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.*
import com.drivequant.drivekit.ui.drivingconditions.component.summary.DrivingConditionsSummaryCardViewModel
import java.util.*

internal class DrivingConditionsViewModel(
    application: Application,
    initialSelectedPeriod: DKPeriod?,
    initialSelectedDate: Date?
) : AndroidViewModel(application) {
    val periodSelectorViewModel = DKPeriodSelectorViewModel()
    val dateSelectorViewModel = DKDateSelectorViewModel()
    val summaryCardViewModel = DrivingConditionsSummaryCardViewModel()
    val syncStatus = MutableLiveData<Any>()
    val updateData = MutableLiveData<Any>()
    private val periods = listOf(DKPeriod.WEEK, DKPeriod.MONTH, DKPeriod.YEAR)
    private var timelineByPeriod: Map<DKPeriod, DKDriverTimeline> = mapOf()
    var selectedDate: Date? = initialSelectedDate
        private set
    private val selectedPeriod: DKPeriod
        get() = this.periodSelectorViewModel.selectedPeriod

    init {
        configurePeriodSelector(initialSelectedPeriod ?: this.selectedPeriod)
        configureDateSelector()
        DriveKitDriverData.getDriverTimelines(
            this.periods,
            SynchronizationType.CACHE
        ) { status, timelines ->
            if (status == TimelineSyncStatus.CACHE_DATA_ONLY) {
                this.timelineByPeriod = timelines.associateBy { it.period }
                update()
            }
        }

        if (initialSelectedPeriod == null && initialSelectedDate == null) {
            updateData()
        } else {
            // Do not sync in this case because the timeline as probably been synchronized previously.
            syncStatus.postValue(Any())
        }
    }

    fun updateData() {
        DriveKitDriverData.getDriverTimelines(this.periods, SynchronizationType.DEFAULT) { _, timelines ->
            this.timelineByPeriod = timelines.associateBy { it.period }
            update(true)
            syncStatus.postValue(Any())
        }
    }

    private fun update(resettingSelectedDate: Boolean = false) {
        getTimelineSource()?.let { timelineSource ->
            if (resettingSelectedDate) {
                selectedDate = null
            }
            val dates = timelineSource.allContext.map { it.date }
            if (dates.isNotEmpty()) {
                val selectedDateIndex: Int = this.selectedDate?.let {
                    val index = dates.indexOf(it)
                    if (index < 0) {
                        null
                    } else {
                        index
                    }
                } ?: (dates.size - 1)

                val date = dates[selectedDateIndex]
                this.selectedDate = date

                this.dateSelectorViewModel.configure(dates, selectedDateIndex, this.selectedPeriod)
                timelineSource.allContext[selectedDateIndex].let {
                    this.summaryCardViewModel.configure(it.numberTripTotal, it.distance)
                }
            } else {
                val date = when (this.selectedPeriod) {
                    DKPeriod.WEEK -> Date().startingFrom(CalendarField.WEEK)
                    DKPeriod.MONTH -> Date().startingFrom(CalendarField.MONTH)
                    DKPeriod.YEAR -> Date().startingFrom(CalendarField.YEAR)
                }
                this.selectedDate = date
                this.dateSelectorViewModel.configure(listOf(date), 0, this.selectedPeriod)
            }
        } ?: run {
            configureWithNoData()
        }
        this.updateData.postValue(Any())
    }

    private fun configureWithNoData() {
        configureEmptyDateSelector()
        this.summaryCardViewModel.configure()
    }

    private fun configurePeriodSelector(selectedPeriod: DKPeriod) {
        this.periodSelectorViewModel.configure(periods)
        this.periodSelectorViewModel.select(selectedPeriod)
        this.periodSelectorViewModel.onPeriodSelected = { oldPeriod, newPeriod ->
            val selectedDate = this.selectedDate
            val sourceTimeline = getTimelineSource(newPeriod)
            if (selectedDate != null && sourceTimeline != null) {
                this.selectedDate = DKDateSelectorViewModel.newSelectedDate(selectedDate, oldPeriod, sourceTimeline.allContext.map { it.date }) { _, _ -> true }
            }
            update()
        }
    }

    private fun configureDateSelector() {
        configureEmptyDateSelector()
        this.dateSelectorViewModel.onDateSelected = { date ->
            updateSelectedDate(date)
        }
    }

    private fun configureEmptyDateSelector() {
        val startDate = when (this.selectedPeriod) {
            DKPeriod.WEEK -> Date().startingFrom(CalendarField.WEEK)
            DKPeriod.MONTH -> Date().startingFrom(CalendarField.MONTH)
            DKPeriod.YEAR -> Date().startingFrom(CalendarField.YEAR)
        }
        dateSelectorViewModel.configure(listOf(startDate), 0, this.selectedPeriod)
    }

    private fun updateSelectedDate(date: Date) {
        if (this.selectedDate != date) {
            this.selectedDate = date
            update()
        }
    }

    private fun getTimelineSource(period: DKPeriod = this.selectedPeriod) = this.timelineByPeriod[period]

    class DrivingConditionsViewModelFactory(
        private val application: Application,
        private val selectedPeriod: DKPeriod?,
        private val selectedDate: Date?
    ) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DrivingConditionsViewModel(application, selectedPeriod, selectedDate) as T
        }
    }
}
