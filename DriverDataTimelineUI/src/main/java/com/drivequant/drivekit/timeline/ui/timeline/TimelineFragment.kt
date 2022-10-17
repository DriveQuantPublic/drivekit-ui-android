package com.drivequant.drivekit.timeline.ui.timeline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.timeline.ui.DriverDataTimelineUI
import com.drivequant.drivekit.timeline.ui.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_timeline.*

class TimelineFragment : Fragment() {

    private lateinit var viewModel: TimelineViewModel

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
        setupTabLayout()
    }

    override fun onResume() {
        super.onResume()
        checkViewModelInitialization()
    }

    private fun setupTabLayout() {
        for (timelineScoreType in viewModel.timelineScoreTypes) {
            val tab = tab_layout_timeline.newTab()
            val icon = DKResource.convertToDrawable(requireContext(), timelineScoreType.iconId)
            icon?.let {
                tab.setIcon(it)
            }
            tab_layout_timeline.addTab(tab)
        }

        for (i in 0 until tab_layout_timeline.tabCount) {
            val tab = tab_layout_timeline.getTabAt(i)
            tab?.setCustomView(R.layout.dk_icon_view_tab)
        }

        if (DriverDataTimelineUI.scores.size < 2) {
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
                    viewModel.selectedTimelineScoreType.scoreType =
                        DriverDataTimelineUI.scores[tab_layout_timeline.selectedTabPosition]
                    updateTimeline()
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    fun updateTimeline() {
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
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(TimelineViewModel::class.java)
        }
    }
}