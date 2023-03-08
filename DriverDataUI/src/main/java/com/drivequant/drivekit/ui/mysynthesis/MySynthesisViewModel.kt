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
import com.drivequant.drivekit.driverdata.community.statistics.CommunityStatisticsStatus
import com.drivequant.drivekit.driverdata.community.statistics.DKCommunityStatistics
import com.drivequant.drivekit.driverdata.community.statistics.DKScoreStatistics
import com.drivequant.drivekit.driverdata.timeline.*
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.mysynthesis.component.community.MySynthesisGaugeViewModel
import com.drivequant.drivekit.ui.mysynthesis.component.scorecard.MySynthesisScoreCardViewModel
import java.util.*

internal class MySynthesisViewModel(application: Application) : AndroidViewModel(application) {

    private val scores = DriverDataUI.scores
    private val periods = listOf(DKPeriod.WEEK, DKPeriod.MONTH, DKPeriod.YEAR)
    val periodSelectorViewModel = DKPeriodSelectorViewModel()
    val scoreSelectorViewModel = DKScoreSelectorViewModel()
    val dateSelectorViewModel = DKDateSelectorViewModel()
    val scoreCardViewModel = MySynthesisScoreCardViewModel()
    val communityGaugeViewModel = MySynthesisGaugeViewModel()
    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()
    val updateData = MutableLiveData<Any>()
    val selectedScore: DKScoreType
        get() = this.scoreSelectorViewModel.selectedScore
    private val selectedPeriod: DKPeriod
        get() = this.periodSelectorViewModel.selectedPeriod
    private var selectedDate: Date? = null
    private var timelineByPeriod: Map<DKPeriod, DKDriverTimeline> = mapOf()
    private var communityStatistics: DKCommunityStatistics? = null

    init {
        configureScoreSelector()
        configurePeriodSelector()
        configureDateSelector()
        DriveKitDriverData.getDriverTimelines(this.periods, SynchronizationType.CACHE) { status, timelines ->
            if (status == TimelineSyncStatus.CACHE_DATA_ONLY) {
                this.timelineByPeriod = timelines.associateBy { it.period }
                update()
            }
        }
        DriveKitDriverData.getCommunityStatistics(SynchronizationType.CACHE) { status, statistics ->
            if (status == CommunityStatisticsStatus.CACHE_DATA_ONLY) {
                this.communityStatistics = statistics
                updateCommunityGauge()
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
        DriveKitDriverData.getCommunityStatistics(SynchronizationType.DEFAULT) { _, statistics ->
            this.communityStatistics = statistics
            updateCommunityGauge()
        }
    }

    private fun update(resettingSelectedDate: Boolean = false) {
        getTimelineSource()?.let { timelineSource ->
            if (resettingSelectedDate) {
                selectedDate = null
            }
            val dates = timelineSource.allContext.mapNotNull { allContextItem ->
                if (allContextItem.date == this.selectedDate || allContextItem.hasValueForScoreType(this.selectedScore)) {
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
                val date = dates[selectedDateIndex]
                this.selectedDate = date
                this.dateSelectorViewModel.configure(dates, selectedDateIndex, this.selectedPeriod)

                val scoreSynthesis = getDriverScoreSynthesis(timelineSource, this.selectedScore, date)
                this.scoreCardViewModel.configure(
                    this.selectedScore,
                    this.selectedPeriod,
                    scoreSynthesis,
                    timelineSource.allContext.first { it.date == this.selectedDate }
                )
                updateCommunityGauge(scoreSynthesis)
            }
        } ?: run {
            configureWithNoData()
        }
        this.updateData.postValue(Any())
    }

    private fun getDriverScoreSynthesis(selectedScore: DKScoreType, date: Date): DKScoreSynthesis? =
        getDriverScoreSynthesis(getTimelineSource(), selectedScore, date)

    private fun getDriverScoreSynthesis(
        timeline: DKDriverTimeline?,
        selectedScore: DKScoreType,
        date: Date
    ): DKScoreSynthesis? = timeline?.getDriverScoreSynthesis(selectedScore, date)

    private fun configureWithNoData() {
        when (this.selectedPeriod) {
            DKPeriod.WEEK -> Date().startingFrom(CalendarField.WEEK)
            DKPeriod.MONTH -> Date().startingFrom(CalendarField.MONTH)
            DKPeriod.YEAR -> Date().startingFrom(CalendarField.YEAR)
        }.let { startDate ->
            dateSelectorViewModel.configure(listOf(startDate), 0, this.selectedPeriod)
            scoreCardViewModel.configure(this.selectedScore, this.selectedPeriod, null, null)
            updateCommunityGauge()
        }
    }

    private fun configureScoreSelector() {
        this.scoreSelectorViewModel.configure(this.scores) { _ ->
            update()
        }
    }

    private fun configurePeriodSelector() {
        this.periodSelectorViewModel.configure(periods)
        this.periodSelectorViewModel.select(this.selectedPeriod)
        this.periodSelectorViewModel.onPeriodSelected = { oldPeriod, newPeriod ->
            val selectedDate = this.selectedDate
            val sourceTimeline = getTimelineSource(newPeriod)
            if (selectedDate != null && sourceTimeline != null) {
                this.selectedDate = DKDateSelectorViewModel.newSelectedDate(selectedDate, oldPeriod, sourceTimeline.allContext.map { it.date }) { _, date ->
                    sourceTimeline.allContextItemAt(date)?.hasValueForScoreType(this.selectedScore) ?: false
                }
            }
            update()
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

    private fun updateCommunityGauge() {
        val scoreSynthesis: DKScoreSynthesis? = this.selectedDate?.let { getDriverScoreSynthesis(this.selectedScore, it) }
        updateCommunityGauge(scoreSynthesis)
    }

    private fun updateCommunityGauge(scoreSynthesis: DKScoreSynthesis?) {
        this.communityStatistics?.let { communityStatistics ->
            val scoreStatistics: DKScoreStatistics = when (this.selectedScore) {
                DKScoreType.SAFETY -> communityStatistics.safety
                DKScoreType.ECO_DRIVING -> communityStatistics.ecoDriving
                DKScoreType.DISTRACTION -> communityStatistics.distraction
                DKScoreType.SPEEDING -> communityStatistics.speeding
            }
            this.communityGaugeViewModel.configure(this.selectedScore, scoreSynthesis?.scoreValue, scoreStatistics.min, scoreStatistics.mean, scoreStatistics.max)
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
