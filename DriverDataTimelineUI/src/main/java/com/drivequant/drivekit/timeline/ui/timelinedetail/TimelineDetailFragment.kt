package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorView
import com.drivequant.drivekit.timeline.ui.component.graph.view.TimelineGraphView
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorView
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextView
import com.google.gson.Gson
import java.util.*

class TimelineDetailFragment : Fragment() {

    internal lateinit var viewModel: TimelineDetailViewModel
        private set

    private lateinit var selectedScore: DKScoreType
    private lateinit var selectedPeriod: DKTimelinePeriod
    private lateinit var selectedDate: Date
    private lateinit var weekTimeline: Timeline
    private lateinit var monthTimeline: Timeline

    private lateinit var periodSelectorContainer: LinearLayout
    private lateinit var periodSelectorView: PeriodSelectorView

    private lateinit var dateSelectorContainer: LinearLayout
    private lateinit var dateSelectorView: DateSelectorView

    private lateinit var roadContextContainer: LinearLayout
    private lateinit var roadContextView: RoadContextView

    private lateinit var graphContainer: LinearLayout
    private var graphViews = mutableListOf<TimelineGraphView>()

    private lateinit var nestedScrollView: NestedScrollView

    companion object {
        fun newInstance(
            selectedScore: DKScoreType,
            selectedPeriod: DKTimelinePeriod,
            selectedDate: Date,
            weekTimeline: Timeline,
            monthTimeline: Timeline
        ): TimelineDetailFragment {
            val fragment = TimelineDetailFragment()
            fragment.selectedScore = selectedScore
            fragment.selectedPeriod = selectedPeriod
            fragment.selectedDate = selectedDate
            fragment.weekTimeline = weekTimeline
            fragment.monthTimeline = monthTimeline
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_timeline_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        periodSelectorContainer = view.findViewById(R.id.period_selector_container)
        dateSelectorContainer = view.findViewById(R.id.date_selector_container)
        roadContextContainer = view.findViewById(R.id.road_context_container)
        graphContainer = view.findViewById(R.id.graph_container)
        nestedScrollView = view.findViewById(R.id.nested_scroll_view)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selectedScore", selectedScore.name)
        outState.putString("selectedPeriod", selectedPeriod.name)
        outState.putLong("selectedDate", selectedDate.time)
        outState.putString("weekTimeline", Gson().toJson(weekTimeline))
        outState.putString("monthTimeline", Gson().toJson(monthTimeline))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            it.getString("selectedScore")?.let { savedScore ->
                selectedScore = DKScoreType.valueOf(savedScore)
            }
            it.getString("selectedPeriod")?.let { savedPeriod ->
                selectedPeriod = DKTimelinePeriod.valueOf(savedPeriod)
            }
            val savedDate = it.getLong("selectedDate")
            if (savedDate > 0) {
                val date = Date()
                date.time = savedDate
                selectedDate = date
            }
            it.getString("weekTimeline")?.let { savedWeekTimeline ->
                weekTimeline = Gson().fromJson(savedWeekTimeline, Timeline::class.java)
            }
            it.getString("monthTimeline")?.let { savedMonthTimeline ->
                monthTimeline = Gson().fromJson(savedMonthTimeline, Timeline::class.java)
            }
        }

        /*nestedScrollView.setOnTouchListener { _, motionEvent ->
            //graphView.manageTouchEvent(motionEvent)
        }*/

        initViewModel()

        activity?.setTitle(viewModel.titleId)

        viewModel.updateData.observe(this) {
            periodSelectorView.configure(viewModel.periodSelectorViewModel)
            roadContextView.configure(viewModel.roadContextViewModel)
            dateSelectorView.configure(viewModel.dateSelectorViewModel)

            configureGraphContainer()
        }

        displayPeriodContainer()
        displayDateContainer()
        displayRoadContextContainer()
    }

    private fun configureGraphContainer() {
        context?.let { context ->
            graphContainer.removeAllViews()
            viewModel.timelineGraphViewModelByScoreItem.forEach {
                val graph = TimelineGraphView(context, it.value)
                graphViews.add(graph)
                graphContainer.addView(graph)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tagScreen()
    }

    private fun tagScreen() {
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_timeline_score_detail"
            ), javaClass.simpleName
        )
    }

    private fun displayPeriodContainer() {
        context?.let {
            periodSelectorView = PeriodSelectorView(it)
            periodSelectorView.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }
            periodSelectorContainer.addView(periodSelectorView)
        }
    }

    private fun displayDateContainer() {
        context?.let {
            dateSelectorView = DateSelectorView(it)
            dateSelectorView.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }
            dateSelectorContainer.addView(dateSelectorView)
        }
    }

    private fun displayRoadContextContainer() {
        context?.let {
            roadContextView = RoadContextView(it)
            roadContextContainer.addView(roadContextView)
        }
        viewModel.roadContextViewModel.changeObserver.observe(this) {
            roadContextView.configure(viewModel.roadContextViewModel)
        }
    }

    private fun initViewModel() {
        if (!this::viewModel.isInitialized) {
            activity?.application?.let { application ->
                if (!this::viewModel.isInitialized) {
                    viewModel = ViewModelProviders.of(
                        this,
                        TimelineDetailViewModel.TimelineDetailViewModelFactory(
                            application,
                            selectedScore,
                            selectedPeriod,
                            selectedDate,
                            weekTimeline,
                            monthTimeline
                        )
                    ).get(TimelineDetailViewModel::class.java)
                }
            }
        }
    }
}
