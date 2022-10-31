package com.drivequant.drivekit.timeline.ui.timeline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorListener
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorView
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextView
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
import com.drivequant.drivekit.timeline.ui.timelinedetail.TimelineDetailActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_timeline.*

class TimelineFragment : Fragment(), PeriodSelectorListener {

    private lateinit var viewModel: TimelineViewModel
    private lateinit var roadContextViewModel: RoadContextViewModel
    private val periodSelectorViews = mutableListOf<PeriodSelectorView>()

    private lateinit var roadContextContainer: LinearLayout
    private lateinit var roadContextView: RoadContextView

    companion object {
        fun newInstance() = TimelineFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_timeline, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roadContextContainer = view.findViewById(R.id.road_context_container)

        checkViewModelInitialization()

        viewModel.updateData.observe(this) {
            roadContextView.configure(viewModel.roadContextViewModel)
        }

        setupSwipeToRefresh()

        displayTabLayout()
        displayPeriodContainer()

        displayDateContainer()
        displayRoadContextContainer()
        displayGraphContainer()

        displayTimelineDetail()
        updateTimeline()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.syncStatus.observe(this) {
            updateProgressVisibility(false)
            Toast.makeText(context, "Sync status: ${it.name}", Toast.LENGTH_SHORT).show() //TODO dev only
        }

        viewModel.timelineDataLiveData.observe(this) {
            Toast.makeText(context, "TimelineLiveData ${it.period.name}", Toast.LENGTH_SHORT).show() //TODO dev only
        }
    }

    private fun displayTimelineDetail() {
        button_display_timeline_detail.setOnClickListener {
            TimelineDetailActivity.launchActivity(requireActivity())
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
        periodSelectorViews.apply {
            addAll(viewModel.timelinePeriodTypes.map {
                PeriodSelectorView(requireContext(), it, this@TimelineFragment)
            })
            forEach {
                period_container.addView(it)
            }
            first().setPeriodSelected(true)
        }
    }

    private fun displayGraphContainer() {
        // TODO()
    }

    private fun displayDateContainer() {
        // TODO()
    }

    private fun displayRoadContextContainer() {
        if(!this::roadContextViewModel.isInitialized) {
            roadContextViewModel = ViewModelProviders.of(
                this,
                RoadContextViewModel.RoadContextViewModelFactory()
            ).get(RoadContextViewModel::class.java)
            context?.let {
                roadContextView = RoadContextView(it)
                roadContextContainer.addView(roadContextView)
            }
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
            viewModel = ViewModelProviders.of(this).get(TimelineViewModel::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        checkViewModelInitialization()
    }

    override fun onPeriodSelected(period: DKTimelinePeriod) {
        viewModel.updateTimelinePeriod(period)
        periodSelectorViews.forEach {
            if (period == it.timelinePeriod) {
                it.setPeriodSelected(true)
            } else {
                it.setPeriodSelected(false)
            }
        }
    }

    private fun setupSwipeToRefresh() {
        updateSwipeRefreshTripsVisibility(false)
        dk_swipe_refresh_timeline.setOnRefreshListener {
            updateTimeline()
        }
    }

    private fun updateSwipeRefreshTripsVisibility(display: Boolean) {
        dk_swipe_refresh_timeline.apply {
            isRefreshing = display
            if (display) {
                visibility = View.VISIBLE
            }
        }
    }
}