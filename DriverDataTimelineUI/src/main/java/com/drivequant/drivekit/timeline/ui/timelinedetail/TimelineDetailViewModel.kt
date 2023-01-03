package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.timeline.ui.associatedScoreItemTypes
import com.drivequant.drivekit.timeline.ui.cleanedTimeline
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorListener
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorViewModel
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineScoreItemType
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.TimelineGraphViewModel
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorItemListener
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorViewModel
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
import com.drivequant.drivekit.timeline.ui.toTimelineDate
import java.util.Date

internal class TimelineDetailViewModel(
    application: Application/*,
    val selectedScore: DKScoreType,
    val selectedPeriod: DKTimelinePeriod,
    val selectedDate: Date,
    val weekTimeline: Timeline,
    val monthTimeline: Timeline*/
) : AndroidViewModel(application), PeriodSelectorItemListener, DateSelectorListener {

    //TEMP
    val selectedScore: DKScoreType = DKScoreType.SAFETY
    val selectedPeriod: DKTimelinePeriod = DKTimelinePeriod.WEEK
    val selectedDate: Date = Date()
    val weekTimeline: Timeline = TODO()
    val monthTimeline: Timeline = TODO()

    var listener: TimelineDetailViewModelListener? = null
//    val title: String = selectedScore.
    /*
    var localizedTitle: String {
        selectedScore.stringValue()
    }
     */
    private val periodSelectorViewModel: PeriodSelectorViewModel = PeriodSelectorViewModel()
    private val dateSelectorViewModel: DateSelectorViewModel = DateSelectorViewModel()
    private val roadContextViewModel: RoadContextViewModel = RoadContextViewModel()
    private val timelineGraphViewModelByScoreItem: MutableMap<TimelineScoreItemType, TimelineGraphViewModel> = mutableMapOf()
    private val orderedScoreItemTypeToDisplay = this.selectedScore.associatedScoreItemTypes()

    init {
        updateViewModels()
    }

    @Suppress("UNCHECKED_CAST")
    class TimelineDetailViewModelFactory(private val application: Application/*, private val selectedScore: DKScoreType, private val selectedPeriod: DKTimelinePeriod, private val selectedDate: Date, private val weekTimeline: Timeline, private val monthTimeline: Timeline*/) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimelineDetailViewModel(application/*, selectedScore, selectedPeriod, selectedDate, weekTimeline, monthTimeline*/) as T
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
                this.periodSelectorViewModel.configure(this.selectedPeriod)
                this.periodSelectorViewModel.listener = this

                this.dateSelectorViewModel.configure(dates, selectedDateIndex, this.selectedPeriod)
                this.dateSelectorViewModel.listener = this

                this.roadContextViewModel.configure(cleanedTimeline, this.selectedScore, selectedDateIndex)

//                this.timelineGraphViewModelByScoreItem = this.orderedScoreItemTypeToDisplay.reduce()
            }
        }

        /*
        if let selectedDateIndex {
            self.timelineGraphViewModelByScoreItem = self.orderedScoreItemTypeToDisplay.reduce(into: self.timelineGraphViewModelByScoreItem) { partialResult, scoreItemType in
                let timelineGraphViewModel = partialResult[scoreItemType] ?? TimelineGraphViewModel()
                timelineGraphViewModel.configure(
                    timeline: cleanedTimeline,
                    timelineSelectedIndex: selectedDateIndex,
                    graphItem: .scoreItem(scoreItemType),
                    period: selectedPeriod
                )
                timelineGraphViewModel.delegate = self
                partialResult[scoreItemType] = timelineGraphViewModel
            }
        }
         */
    }

    private fun getTimelineSource(): Timeline = when (this.selectedPeriod) {
        DKTimelinePeriod.MONTH -> this.monthTimeline
        DKTimelinePeriod.WEEK -> this.weekTimeline
    }

    override fun onPeriodSelected(period: DKTimelinePeriod) {
        TODO("Not yet implemented")
    }

    override fun onDateSelected(date: Date) {
        TODO("Not yet implemented")
    }
}
