package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorView
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.common.DKPeriod
import com.drivequant.drivekit.databaseutils.entity.DKRawTimeline
import com.drivequant.drivekit.timeline.ui.DispatchTouchLinearLayout
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorView
import com.drivequant.drivekit.timeline.ui.component.graph.view.TimelineGraphView
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextView
import com.google.gson.Gson
import java.util.*

internal class TimelineDetailFragment : Fragment() {

    internal lateinit var viewModel: TimelineDetailViewModel
        private set

    private lateinit var dispatchTouchLinearLayout: DispatchTouchLinearLayout
    private lateinit var selectedScore: DKScoreType
    private lateinit var selectedPeriod: DKPeriod
    private lateinit var selectedDate: Date
    private lateinit var weekTimeline: DKRawTimeline
    private lateinit var monthTimeline: DKRawTimeline

    private lateinit var periodSelectorContainer: LinearLayout
    private lateinit var periodSelectorView: DKPeriodSelectorView

    private lateinit var dateSelectorContainer: LinearLayout
    private lateinit var dateSelectorView: DateSelectorView

    private lateinit var roadContextContainer: LinearLayout
    private lateinit var roadContextView: RoadContextView

    private lateinit var graphContainer: LinearLayout

    companion object {
        fun newInstance(
            selectedScore: DKScoreType,
            selectedPeriod: DKPeriod,
            selectedDate: Date,
            weekTimeline: DKRawTimeline,
            monthTimeline: DKRawTimeline
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
    ): View? = inflater.inflate(R.layout.fragment_timeline_detail, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dispatchTouchLinearLayout = view.findViewById(R.id.dispatch_touch_linear_layout)
        periodSelectorContainer = view.findViewById(R.id.period_selector_container)
        dateSelectorContainer = view.findViewById(R.id.date_selector_container)
        roadContextContainer = view.findViewById(R.id.road_context_container)
        graphContainer = view.findViewById(R.id.graph_container)
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
                selectedPeriod = DKPeriod.valueOf(savedPeriod)
            }
            val savedDate = it.getLong("selectedDate")
            if (savedDate > 0) {
                val date = Date()
                date.time = savedDate
                selectedDate = date
            }
            it.getString("weekTimeline")?.let { savedWeekTimeline ->
                weekTimeline = Gson().fromJson(savedWeekTimeline, DKRawTimeline::class.java)
            }
            it.getString("monthTimeline")?.let { savedMonthTimeline ->
                monthTimeline = Gson().fromJson(savedMonthTimeline, DKRawTimeline::class.java)
            }
        }

        initViewModel()

        activity?.setTitle(viewModel.titleId)

        viewModel.updateData.observe(this) {
            periodSelectorView.configure(viewModel.periodSelectorViewModel)
            roadContextView.configure(viewModel.roadContextViewModel)
            dateSelectorView.configure(viewModel.dateSelectorViewModel)

            configureGraphContainer()
        }

        configurePeriodContainer()
        configureDateContainer()
        configureRoadContextContainer()
    }

    private fun configureGraphContainer() {
        context?.let { context ->
            graphContainer.removeAllViews()
            this.dispatchTouchLinearLayout.removeAllOnInterceptMotionEventListeners()
            viewModel.timelineGraphViewModelByScoreItem.forEach {
                val graph = TimelineGraphView(context, it.value)
                graph.listener = it.value
                graphContainer.addView(graph)

                this.dispatchTouchLinearLayout.addOnInterceptMotionEventListener(graph)
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

    private fun configurePeriodContainer() {
        context?.let {
            periodSelectorView = DKPeriodSelectorView(it, viewModel.periods)
            periodSelectorView.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }
            periodSelectorContainer.apply {
                removeAllViews()
                addView(periodSelectorView)
            }
        }
    }

    private fun configureDateContainer() {
        context?.let {
            dateSelectorView = DateSelectorView(it)
            dateSelectorView.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }
            dateSelectorContainer.apply {
                removeAllViews()
                addView(dateSelectorView)
            }
        }
    }

    private fun configureRoadContextContainer() {
        context?.let {
            roadContextView = RoadContextView(it)
            roadContextContainer.apply {
                removeAllViews()
                addView(roadContextView)
            }
        }
        viewModel.roadContextViewModel.changeObserver.observe(this) {
            roadContextView.configure(viewModel.roadContextViewModel)
        }
    }

    private fun initViewModel() {
        if (!this::viewModel.isInitialized) {
            activity?.application?.let { application ->
                if (!this::viewModel.isInitialized) {
                    viewModel = ViewModelProvider(
                        this,
                        TimelineDetailViewModel.TimelineDetailViewModelFactory(
                            application,
                            selectedScore,
                            selectedPeriod,
                            selectedDate,
                            weekTimeline,
                            monthTimeline
                        )
                    )[TimelineDetailViewModel::class.java]
                }
            }
        }
    }
}
