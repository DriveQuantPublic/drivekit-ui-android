package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorViewModel
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorViewModel
import com.drivequant.drivekit.common.ui.extension.getTitleId
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.timeline.ui.TimelineUtils
import com.drivequant.drivekit.timeline.ui.associatedScoreItemTypes
import com.drivequant.drivekit.timeline.ui.component.graph.GraphItem
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineGraphListener
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineScoreItemType
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.TimelineGraphViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
import java.util.Date

internal class TimelineDetailViewModel(
    application: Application,
    selectedScore: DKScoreType,
    var selectedPeriod: DKPeriod,
    var selectedDate: Date
) : AndroidViewModel(application), TimelineGraphListener {

    val updateData = MutableLiveData<Any>()

    var listener: TimelineDetailViewModelListener? = null
    @StringRes val titleId: Int = selectedScore.getTitleId()
    val periodSelectorViewModel: DKPeriodSelectorViewModel = DKPeriodSelectorViewModel()
    val dateSelectorViewModel: DKDateSelectorViewModel = DKDateSelectorViewModel()
    val roadContextViewModel: RoadContextViewModel = RoadContextViewModel()
    var timelineGraphViewModelByScoreItem: Map<TimelineScoreItemType, TimelineGraphViewModel> = mapOf()
    private val periods = listOf(DKPeriod.WEEK, DKPeriod.MONTH)
    private var timelines = listOf<DKDriverTimeline>()
    private val orderedScoreItemTypeToDisplay = selectedScore.associatedScoreItemTypes()

    init {
        DriveKitDriverData.getDriverTimelines(
            this.periods,
            SynchronizationType.CACHE,
            true
        ) { _, timelines ->
            this.timelines = timelines
            updateViewModels()
        }
    }

    @Suppress("UNCHECKED_CAST")
    class TimelineDetailViewModelFactory(private val application: Application, private val selectedScore: DKScoreType, private val selectedPeriod: DKPeriod, private val selectedDate: Date) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimelineDetailViewModel(application, selectedScore, selectedPeriod, selectedDate) as T
        }
    }

    private fun updateViewModels() {
        getTimelineSource()?.let { selectedTimeline ->
            val dates = selectedTimeline.allContext.map { it.date }
            val selectedDateIndex = dates.indexOf(this.selectedDate)
            if (selectedDateIndex >= 0) {
                // Update view models.
                this.periodSelectorViewModel.configure(this.periods)
                this.periodSelectorViewModel.select(this.selectedPeriod)
                this.periodSelectorViewModel.onPeriodSelected = { _, newPeriod ->
                    onPeriodSelected(newPeriod)
                }

                this.dateSelectorViewModel.configure(
                    dates,
                    selectedDateIndex,
                    this.selectedPeriod
                )
                this.dateSelectorViewModel.onDateSelected = this::onDateSelected

                this.roadContextViewModel.configure(selectedTimeline, this.selectedDate)

                val timelineGraphViewModelByScoreItem: MutableMap<TimelineScoreItemType, TimelineGraphViewModel> =
                    mutableMapOf()
                this.orderedScoreItemTypeToDisplay.forEach { scoreItemType ->
                    val timelineGraphViewModel = TimelineGraphViewModel()
                    timelineGraphViewModel.configure(
                        getApplication(),
                        selectedTimeline,
                        dates,
                        selectedDateIndex,
                        GraphItem.ScoreItem(scoreItemType),
                        this.selectedPeriod
                    )
                    timelineGraphViewModel.listener = this
                    timelineGraphViewModelByScoreItem[scoreItemType] = timelineGraphViewModel
                }
                this.timelineGraphViewModelByScoreItem = timelineGraphViewModelByScoreItem
            }
            updateData.postValue(Any())
        }
    }

    private fun getTimelineSource(period: DKPeriod = this.selectedPeriod): DKDriverTimeline? = when (period) {
        DKPeriod.MONTH,
        DKPeriod.WEEK -> this.timelines.firstOrNull { it.period == period }
        DKPeriod.YEAR -> throw IllegalAccessException("Not managed in Timeline")
    }

    private fun onPeriodSelected(period: DKPeriod) {
        if (this.selectedPeriod != period) {
            getTimelineSource(period)?.let { timeline ->
                val date = TimelineUtils.updateSelectedDate(
                    this.selectedPeriod,
                    this.selectedDate,
                    timeline
                )
                if (date != null) {
                    this.selectedPeriod = period
                    this.selectedDate = date
                    updateViewModels()
                    this.listener?.onUpdateSelectedPeriod(period)
                }
            }
        }
    }

    //- DateSelectorListener

    private fun onDateSelected(date: Date) {
        this.selectedDate = date
        updateViewModels()
        this.listener?.onUpdateSelectedDate(date)
    }

    //- TimelineGraphListener

    override fun onSelectDate(date: Date) {
        this.selectedDate = date
        updateViewModels()
        this.listener?.onUpdateSelectedDate(date)
    }
}
