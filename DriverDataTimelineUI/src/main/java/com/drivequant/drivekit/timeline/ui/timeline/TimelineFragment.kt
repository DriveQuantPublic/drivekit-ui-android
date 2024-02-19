package com.drivequant.drivekit.timeline.ui.timeline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.contextcard.view.DKContextCardView
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorView
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorItemListener
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorView
import com.drivequant.drivekit.common.ui.component.scoreselector.DKScoreSelectorView
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.timeline.ui.DispatchTouchFrameLayout
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.graph.view.TimelineGraphView
import com.drivequant.drivekit.timeline.ui.timelinedetail.TimelineDetailActivity
import java.util.*

internal class TimelineFragment : Fragment(), DKPeriodSelectorItemListener {

    private lateinit var viewModel: TimelineViewModel

    private lateinit var dispatchTouchFrameLayout: DispatchTouchFrameLayout

    private lateinit var scoreSelectorView: DKScoreSelectorView

    private lateinit var periodSelectorContainer: LinearLayout
    private lateinit var periodSelectorView: DKPeriodSelectorView

    private lateinit var dateSelectorContainer: LinearLayout
    private lateinit var dateSelectorView: DKDateSelectorView

    private lateinit var roadContextContainer: LinearLayout
    private lateinit var contextCardView: DKContextCardView

    private lateinit var graphContainer: LinearLayout
    private lateinit var graphView: TimelineGraphView
    private lateinit var displayTimelineDetailButton: Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {
        fun newInstance() = TimelineFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_timeline, container, false).setDKStyle()

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dispatchTouchFrameLayout = view.findViewById(R.id.dispatch_touch_frame_layout)
        scoreSelectorView = view.findViewById(R.id.score_selector_view)
        periodSelectorContainer = view.findViewById(R.id.period_selector_container)
        dateSelectorContainer = view.findViewById(R.id.date_selector_container)
        roadContextContainer = view.findViewById(R.id.road_context_container)
        graphContainer = view.findViewById(R.id.graph_container)
        displayTimelineDetailButton = view.findViewById(R.id.button_display_timeline_detail)
        swipeRefreshLayout = view.findViewById(R.id.dk_swipe_refresh_timeline)

        checkViewModelInitialization()

        viewModel.updateData.observe(viewLifecycleOwner) {
            periodSelectorView.configure(viewModel.periodSelectorViewModel)
            contextCardView.configure(viewModel.roadContextViewModel)

            if (viewModel.dateSelectorViewModel.hasDates()) {
                dateSelectorContainer.visibility = View.VISIBLE
                dateSelectorView.configure(viewModel.dateSelectorViewModel)
            } else {
                dateSelectorContainer.visibility = View.GONE
            }
            viewModel.dateSelectorViewModel.onDateSelected = { date ->
                viewModel.updateTimelineDate(date)
            }

            displayTimelineDetailButton.visibility = if (viewModel.roadContextViewModel.displayData()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        setupSwipeToRefresh()

        configureScoreSelectorView()
        configurePeriodContainer()
        configureDateContainer()
        configureRoadContextContainer()
        configureGraphContainer()

        configureTimelineDetailButton()

        viewModel.syncStatus.observe(viewLifecycleOwner) {
            updateSwipeRefreshTripsVisibility(false)
        }

        updateTimeline()
    }

    fun updateDataFromDetailScreen(selectedPeriod: DKPeriod, selectedDate: Date) {
        viewModel.updateTimelineDateAndPeriod(selectedPeriod, selectedDate)
    }

    private fun configureTimelineDetailButton() {
        displayTimelineDetailButton.apply {
            setOnClickListener {
                TimelineDetailActivity.launchActivity(
                    requireActivity(),
                    viewModel.selectedScore,
                    viewModel.currentPeriod,
                    viewModel.selectedDate
                )
            }
        }
    }

    private fun configureScoreSelectorView() {
        scoreSelectorView.configure(viewModel.scoreSelectorViewModel)
        scoreSelectorView.visibility = if (scoreSelectorView.scoreCount() < 2) View.GONE else View.VISIBLE
    }

    private fun configurePeriodContainer() {
        context?.let {
            periodSelectorView = DKPeriodSelectorView(it)
            periodSelectorView.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                configure(viewModel.periodSelectorViewModel)
            }
            periodSelectorContainer.apply {
                removeAllViews()
                addView(periodSelectorView)
            }
        }
    }

    private fun configureDateContainer() {
        context?.let {
            dateSelectorView = DKDateSelectorView(it)
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
            contextCardView = DKContextCardView(it)
            contextCardView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            roadContextContainer.apply {
                removeAllViews()
                addView(contextCardView)
            }
        }
        viewModel.roadContextViewModel.changeObserver.observe(viewLifecycleOwner) {
            contextCardView.configure(viewModel.roadContextViewModel)
        }
    }

    private fun configureGraphContainer() {
        context?.let {
            graphView = TimelineGraphView(it, this.viewModel.graphViewModel)
            graphView.listener = this.viewModel.graphViewModel
            graphContainer.apply {
                removeAllViews()
                addView(graphView)
            }
            this.dispatchTouchFrameLayout.addOnInterceptMotionEventListener(this.graphView)
        }
    }

    private fun updateTimeline() {
        updateSwipeRefreshTripsVisibility(true)
        viewModel.updateTimeline()
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            activity?.application?.let { application ->
                if (!this::viewModel.isInitialized) {
                    viewModel = ViewModelProvider(
                        this,
                        TimelineViewModel.TimelineViewModelFactory(application)
                    )[TimelineViewModel::class.java]
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tagScreen()
        checkViewModelInitialization()
    }

    private fun tagScreen() {
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_timeline), javaClass.simpleName)
    }

    override fun onPeriodSelected(period: DKPeriod) {
        viewModel.updateTimelinePeriod(period)
    }

    private fun setupSwipeToRefresh() {
        updateSwipeRefreshTripsVisibility(false)
        swipeRefreshLayout.setOnRefreshListener {
            updateTimeline()
        }
    }

    private fun updateSwipeRefreshTripsVisibility(display: Boolean) {
        swipeRefreshLayout.isRefreshing = display
    }
}
