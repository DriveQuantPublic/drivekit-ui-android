package com.drivequant.drivekit.timeline.ui.timeline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorListener
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorView
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextFragment
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextViewModel
import com.drivequant.drivekit.timeline.ui.timelinedetail.TimelineDetailActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_timeline.*

class TimelineFragment : Fragment(), PeriodSelectorListener {

    private lateinit var viewModel: TimelineViewModel
    private lateinit var roadContextViewModel: RoadContextViewModel
    private val periodSelectorViews = mutableListOf<PeriodSelectorView>()

    companion object {
        fun newInstance() = TimelineFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_timeline, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkViewModelInitialization()
        setupSwipeToRefresh()

        displayTabLayout()
        displayDateContainer()
        displayGraphContainer()
        displayPeriodContainer()

        displayTimelineDetail()
        updateTimeline()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.syncStatus.observe(this) {
            updateProgressVisibility(false)
            Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
        }

        viewModel.timelineDataLiveData.observe(this) {
            Toast.makeText(context, it.period.name, Toast.LENGTH_SHORT).show()
            displayRoadContextContainer(it.roadContexts)
        }
    }

    private fun displayTimelineDetail() {
        button_display_timeline_detail.setOnClickListener {
            TimelineDetailActivity.launchActivity(requireActivity())
        }
    }

    private fun displayTabLayout() {
        context?.let { context ->
            viewModel.timelineScoreTypes.forEach {
                val tab = tab_layout_timeline.newTab()
                val icon = DKResource.convertToDrawable(context, it.getIconResId())
                icon?.let { drawable ->
                    tab.setIcon(drawable)
                }
                tab_layout_timeline.addTab(tab)
            }
        }

        for (i in 0 until tab_layout_timeline.tabCount) {
            val tab = tab_layout_timeline.getTabAt(i)
            tab?.setCustomView(R.layout.dk_icon_view_tab)
        }

        if (viewModel.timelineScoreTypes.size < 2) {
            tab_layout_timeline.visibility = View.GONE
        }

        tab_layout_timeline.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dkTimelineBackgroundColor
                )
            )

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewModel.updateTimelineScore(
                        tab_layout_timeline.selectedTabPosition
                    )
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun displayPeriodContainer() {
        periodSelectorViews.addAll(viewModel.timelinePeriodTypes.map {
            PeriodSelectorView(requireContext(), it, this)
        })
        periodSelectorViews.forEach {
            period_container.addView(it)
        }
        periodSelectorViews.first().setPeriodSelected(true)
    }

    private fun displayGraphContainer() {
        // TODO()
    }

    private fun displayDateContainer() {
        // TODO()
    }

    private fun displayRoadContextContainer(roadContextDataItems: List<RoadContextItemData>) {
        if(!this::roadContextViewModel.isInitialized) {
            roadContextViewModel = ViewModelProviders.of(
                this,
                RoadContextViewModel.RoadContextViewModelFactory(
                    roadContextDataItems
                )
            ).get(RoadContextViewModel::class.java)
            childFragmentManager.beginTransaction()
                .replace(
                    R.id.road_context_container,
                    RoadContextFragment.newInstance(roadContextViewModel)
                )
                .commit()
        }
    }

    private fun updateTimeline() {
        updateProgressVisibility(true)
        viewModel.fetchTimeline()
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

    override fun onSelectPeriod(period: DKTimelinePeriod) {
          periodSelectorViews.forEach {
              if (period == it.timelinePeriod) {
                  it.setPeriodSelected(true)
                  viewModel.updateTimelinePeriod(period)
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
        if (display) {
            dk_swipe_refresh_timeline.isRefreshing = display
        } else {
            dk_swipe_refresh_timeline.visibility = View.VISIBLE
            dk_swipe_refresh_timeline.isRefreshing = display
        }
    }
}