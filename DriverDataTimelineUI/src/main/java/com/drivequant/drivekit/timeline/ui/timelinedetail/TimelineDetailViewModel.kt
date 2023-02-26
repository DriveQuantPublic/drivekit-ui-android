package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorViewModel
import com.drivequant.drivekit.databaseutils.entity.DKRawTimeline
import com.drivequant.drivekit.timeline.ui.TimelineUtils
import com.drivequant.drivekit.timeline.ui.associatedScoreItemTypes
import com.drivequant.drivekit.timeline.ui.cleanedTimeline
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorViewModel
import com.drivequant.drivekit.common.ui.extension.getTitleId
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.timeline.ui.component.graph.GraphItem
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineGraphListener
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineScoreItemType
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.TimelineGraphViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
import com.drivequant.drivekit.timeline.ui.toTimelineDate
import java.util.Date

internal class TimelineDetailViewModel(
    application: Application,
    var selectedScore: DKScoreType,
    var selectedPeriod: DKPeriod,
    var selectedDate: Date,
    var weekTimeline: DKRawTimeline,
    var monthTimeline: DKRawTimeline
) : AndroidViewModel(application), TimelineGraphListener {

    val periods = listOf(DKPeriod.WEEK, DKPeriod.MONTH)

    val updateData = MutableLiveData<Any>()

    var listener: TimelineDetailViewModelListener? = null
    @StringRes val titleId: Int = selectedScore.getTitleId()
    val periodSelectorViewModel: DKPeriodSelectorViewModel = DKPeriodSelectorViewModel()
    val dateSelectorViewModel: DKDateSelectorViewModel = DKDateSelectorViewModel()
    val roadContextViewModel: RoadContextViewModel = RoadContextViewModel()
    var timelineGraphViewModelByScoreItem: Map<TimelineScoreItemType, TimelineGraphViewModel> = mapOf()
    private val orderedScoreItemTypeToDisplay = this.selectedScore.associatedScoreItemTypes()

    init {
        updateViewModels()
    }

    @Suppress("UNCHECKED_CAST")
    class TimelineDetailViewModelFactory(private val application: Application, private val selectedScore: DKScoreType, private val selectedPeriod: DKPeriod, private val selectedDate: Date, private val weekTimeline: DKRawTimeline, private val monthTimeline: DKRawTimeline) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimelineDetailViewModel(application, selectedScore, selectedPeriod, selectedDate, weekTimeline, monthTimeline) as T
        }
    }

    private fun updateViewModels() {
        val selectedTimeline = getTimelineSource()
        val sourceDates = selectedTimeline.allContext.date.map { it.toTimelineDate()!! }
        var selectedDateIndex = sourceDates.indexOf(this.selectedDate)
        if (selectedDateIndex >= 0) {
            val cleanedTimeline =
                selectedTimeline.cleanedTimeline(this.selectedScore, selectedDateIndex)

            // Compute selected index.
            val dates = cleanedTimeline.allContext.date.map { it.toTimelineDate()!! }
            selectedDateIndex = dates.indexOf(this.selectedDate)

            // Update view models.
            if (selectedDateIndex >= 0) {
                this.periodSelectorViewModel.select(this.selectedPeriod)
                this.periodSelectorViewModel.onPeriodSelected = this::onPeriodSelected

                this.dateSelectorViewModel.configure(dates, selectedDateIndex, this.selectedPeriod)
                this.dateSelectorViewModel.onDateSelected = this::onDateSelected

                this.roadContextViewModel.configure(cleanedTimeline, this.selectedScore, selectedDateIndex)

                val timelineGraphViewModelByScoreItem: MutableMap<TimelineScoreItemType, TimelineGraphViewModel> = mutableMapOf()
                this.orderedScoreItemTypeToDisplay.forEach { scoreItemType ->
                    val timelineGraphViewModel = TimelineGraphViewModel()
                    timelineGraphViewModel.configure(getApplication(), cleanedTimeline, selectedDateIndex, GraphItem.ScoreItem(scoreItemType), this.selectedPeriod)
                    timelineGraphViewModel.listener = this
                    timelineGraphViewModelByScoreItem[scoreItemType] = timelineGraphViewModel
                }
                this.timelineGraphViewModelByScoreItem = timelineGraphViewModelByScoreItem
            }
        }
        updateData.postValue(Any())
    }

    private fun getTimelineSource(): DKRawTimeline = when (this.selectedPeriod) {
        DKPeriod.MONTH -> this.monthTimeline
        DKPeriod.WEEK -> this.weekTimeline
        DKPeriod.YEAR -> throw IllegalAccessException("Not managed in Timeline")
    }

    private fun onPeriodSelected(period: DKPeriod) {
        if (this.selectedPeriod != period) {
            val date = TimelineUtils.updateSelectedDateForNewPeriod(period, this.selectedDate, this.weekTimeline, this.monthTimeline)
            if (date != null) {
                this.selectedPeriod = period
                this.selectedDate = date
                updateViewModels()
                this.listener?.onUpdateSelectedPeriod(period)
            }
        }
    }

    //- DateSelectorListener

    fun onDateSelected(date: Date) {
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
