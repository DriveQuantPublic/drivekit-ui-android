package com.drivequant.drivekit.timeline.ui.timeline

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.timeline.ui.DispatchTouchFrameLayout
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorListener
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorView
import com.drivequant.drivekit.timeline.ui.component.graph.view.TimelineGraphView
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorItemListener
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorView
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextView
import com.drivequant.drivekit.timeline.ui.timelinedetail.TimelineDetailActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_timeline.*
import java.util.*

class TimelineFragment : Fragment(), PeriodSelectorItemListener {

    private lateinit var viewModel: TimelineViewModel

    private lateinit var dispatchTouchFrameLayout: DispatchTouchFrameLayout

    private lateinit var periodSelectorContainer: LinearLayout
    private lateinit var periodSelectorView: PeriodSelectorView

    private lateinit var dateSelectorContainer: LinearLayout
    private lateinit var dateSelectorView: DateSelectorView

    private lateinit var roadContextContainer: LinearLayout
    private lateinit var roadContextView: RoadContextView

    private lateinit var graphContainer: LinearLayout
    private lateinit var graphView: TimelineGraphView

    companion object {
        fun newInstance() = TimelineFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_timeline, container, false)

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dispatchTouchFrameLayout = view.findViewById(R.id.dispatch_touch_frame_layout)
        periodSelectorContainer = view.findViewById(R.id.period_selector_container)
        dateSelectorContainer = view.findViewById(R.id.date_selector_container)
        roadContextContainer = view.findViewById(R.id.road_context_container)
        graphContainer = view.findViewById(R.id.graph_container)

        checkViewModelInitialization()

        viewModel.updateData.observe(this) {
            periodSelectorView.configure(viewModel.periodSelectorViewModel)
            roadContextView.configure(viewModel.roadContextViewModel)

            if (viewModel.dateSelectorViewModel.hasDates()) {
                dateSelectorContainer.visibility = View.VISIBLE
                dateSelectorView.configure(viewModel.dateSelectorViewModel)
            } else {
                dateSelectorContainer.visibility = View.GONE
            }
            viewModel.dateSelectorViewModel.listener = object : DateSelectorListener {
                override fun onDateSelected(date: Date) {
                    viewModel.updateTimelineDate(date)
                }
            }

            button_display_timeline_detail.visibility = if (viewModel.roadContextViewModel.displayData()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        setupSwipeToRefresh()

        displayTabLayout()
        displayPeriodContainer()
        displayDateContainer()
        displayRoadContextContainer()
        displayGraphContainer()

        setupTimelineDetail()
        updateTimeline()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.syncStatus.observe(this) {
            updateProgressVisibility(false)
        }
    }

    fun updateDataFromDetailScreen(selectedPeriod: DKTimelinePeriod, selectedDate: Date) {
        onPeriodSelected(selectedPeriod)
        viewModel.updateTimelineDate(selectedDate)
    }

    private fun setupTimelineDetail() {
        button_display_timeline_detail.apply {
            setOnClickListener {
                TimelineDetailActivity.launchActivity(
                    requireActivity(),
                    viewModel.selectedScore,
                    viewModel.currentPeriod,
                    viewModel.selectedDate,
                    viewModel.weekTimeline,
                    viewModel.monthTimeline
                )
            }
            headLine2(DriveKitUI.colors.secondaryColor())
        }
    }

    private fun displayTabLayout() {
        context?.let { context ->
            viewModel.scores.forEach {
                val tab = tab_layout_timeline.newTab()
                val icon = DKResource.convertToDrawable(context, it.getIconResId())
                icon?.let { drawable ->
                    tab.setIcon(drawable)
                }
                tab_layout_timeline.addTab(tab)
            }
        }

        for (i in 0 until tab_layout_timeline.tabCount) {
            tab_layout_timeline.getTabAt(i)?.setCustomView(R.layout.dk_icon_view_tab)
        }

        if (viewModel.scores.size < 2) {
            tab_layout_timeline.visibility = View.GONE
        }

        tab_layout_timeline.apply {
            setSelectedTabIndicatorColor(DriveKitUI.colors.secondaryColor())
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dkTimelineBackgroundColor))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewModel.updateTimelineScore(tab_layout_timeline.selectedTabPosition)
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
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

    private fun displayGraphContainer() {
        context?.let {
            graphView = TimelineGraphView(it, this.viewModel.graphViewModel)
            graphView.listener = this.viewModel.graphViewModel
            graphContainer.addView(graphView)

            this.dispatchTouchFrameLayout.interceptMotionEventListener = this.graphView
        }
    }

    private fun updateTimeline() {
        updateProgressVisibility(true)
        viewModel.updateTimeline()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        progress_circular?.apply {
            visibility = if (displayProgress) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        updateSwipeRefreshTripsVisibility(displayProgress)
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            activity?.application?.let { application ->
                if (!this::viewModel.isInitialized) {
                    viewModel = ViewModelProviders.of(
                        this,
                        TimelineViewModel.TimelineViewModelFactory(application)
                    ).get(TimelineViewModel::class.java)
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
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_timeline"
            ), javaClass.simpleName
        )
    }

    override fun onPeriodSelected(period: DKTimelinePeriod) {
        viewModel.updateTimelinePeriod(period)
    }

    private fun setupSwipeToRefresh() {
        updateSwipeRefreshTripsVisibility(false)
        dk_swipe_refresh_timeline.setOnRefreshListener {
            updateTimeline()
        }
    }

    private fun updateSwipeRefreshTripsVisibility(display: Boolean) {
        dk_swipe_refresh_timeline.isRefreshing = display
    }
}
