package com.drivequant.drivekit.ui.mysynthesis

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
import com.drivequant.drivekit.driverdata.community.statistics.CommunityStatisticsStatus
import com.drivequant.drivekit.driverdata.community.statistics.DKCommunityStatistics
import com.drivequant.drivekit.driverdata.timeline.*
import com.drivequant.drivekit.ui.mysynthesis.component.communitycard.MySynthesisCommunityCardViewModel
import com.drivequant.drivekit.ui.mysynthesis.component.scorecard.MySynthesisScoreCardViewModel
import java.util.*

internal class MySynthesisViewModel(application: Application) : AndroidViewModel(application) {

    private val scores = DriveKitUI.scores
    private val periods = listOf(DKPeriod.WEEK, DKPeriod.MONTH, DKPeriod.YEAR)
    val periodSelectorViewModel = DKPeriodSelectorViewModel()
    val scoreSelectorViewModel = DKScoreSelectorViewModel()
    val dateSelectorViewModel = DKDateSelectorViewModel()
    val scoreCardViewModel = MySynthesisScoreCardViewModel()
    private val selectedScore: DKScoreType
        get() = this.scoreSelectorViewModel.selectedScore
    private val selectedPeriod: DKPeriod
        get() = this.periodSelectorViewModel.selectedPeriod
    val communityCardViewModel = MySynthesisCommunityCardViewModel()
    val syncStatus = MutableLiveData<Any>()
    val updateData = MutableLiveData<Any>()
    private var selectedDate: Date? = null
    private var timelineByPeriod: Map<DKPeriod, DKDriverTimeline> = mapOf()
    private var communityStatistics: DKCommunityStatistics? = null

    init {
        configureScoreSelector()
        configurePeriodSelector()
        configureDateSelector()
        configureScoreCardViewModel()
        configureCommunityCardViewModel()

        DriveKitDriverData.getDriverTimelines(
            this.periods,
            ignoreItemsWithoutTripScored = true,
            SynchronizationType.CACHE
        ) { status, timelines ->
            if (status == TimelineSyncStatus.CACHE_DATA_ONLY) {
                this.timelineByPeriod = timelines.associateBy { it.period }

                DriveKitDriverData.getCommunityStatistics(SynchronizationType.CACHE) { communityStatus, statistics ->
                    if (communityStatus == CommunityStatisticsStatus.CACHE_DATA_ONLY) {
                        this.communityStatistics = statistics
                    }
                    update()
                }
            }
        }
        updateData()
    }

    fun updateData() {
        DriveKitDriverData.getDriverTimelines(
            this.periods,
            ignoreItemsWithoutTripScored = true,
            SynchronizationType.DEFAULT
        ) { _, timelines ->
            this.timelineByPeriod = timelines.associateBy { it.period }
            DriveKitDriverData.getCommunityStatistics { _, statistics ->
                this.communityStatistics = statistics
                update(true)
                syncStatus.postValue(Any())
            }
        }
    }

    fun updateTimelineDateAndPeriod(period: DKPeriod, date: Date) {
        var shouldUpdate = false
        if (this.selectedPeriod != period) {
            shouldUpdate = true
            this.periodSelectorViewModel.select(period)
        }
        if (this.selectedDate != date) {
            shouldUpdate = true
            this.selectedDate = date
        }
        if (shouldUpdate) {
            update()
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

                updateScoreCardViewModel()
                updateCommunityCardViewModel()
            } else {
                val date = when (this.selectedPeriod) {
                    DKPeriod.WEEK -> Date().startingFrom(CalendarField.WEEK)
                    DKPeriod.MONTH -> Date().startingFrom(CalendarField.MONTH)
                    DKPeriod.YEAR -> Date().startingFrom(CalendarField.YEAR)
                }
                this.selectedDate = date
                this.dateSelectorViewModel.configure(listOf(date), 0, this.selectedPeriod)

                updateScoreCardViewModel()
                updateCommunityCardViewModel()
            }
        } ?: run {
            configureWithNoData()
        }
        this.updateData.postValue(Any())
    }

    private fun configureWithNoData() {
        configureEmptyDateSelector()
        scoreCardViewModel.configure(
            this.selectedScore,
            this.selectedPeriod,
            scoreSynthesis = null,
            previousPeriodDate = null
        )
        communityCardViewModel.configure(this.selectedScore, this.selectedPeriod, null, null, this.communityStatistics ?: DKCommunityStatistics.buildDefault())
    }

    private fun configureScoreSelector() {
        this.scoreSelectorViewModel.configure(this.scores) {
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

    private fun configureScoreCardViewModel() {
        updateScoreCardViewModel()
    }

    private fun updateScoreCardViewModel() {
        var scoreSynthesis: DKScoreSynthesis? = null
        var previousDate: Date? = null

        this.selectedDate?.let {
            val timeline = getTimelineSource()
            scoreSynthesis = timeline?.getDriverScoreSynthesis(this.selectedScore, it)
            previousDate = timeline?.previousAllContextItemFrom(it)?.date
        }
        this.scoreCardViewModel.configure(
            score = this.selectedScore,
            period = this.selectedPeriod,
            scoreSynthesis = scoreSynthesis,
            previousPeriodDate = previousDate
        )
    }

    private fun configureCommunityCardViewModel() {
        updateCommunityCardViewModel()
    }

    private fun updateCommunityCardViewModel() {
        this.communityCardViewModel.configure(
            scoreType = this.selectedScore,
            period = this.selectedPeriod,
            driverTimeline = getTimelineSource(),
            selectedDate = this.selectedDate,
            statistics = this.communityStatistics ?: DKCommunityStatistics.buildDefault()
        )
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
