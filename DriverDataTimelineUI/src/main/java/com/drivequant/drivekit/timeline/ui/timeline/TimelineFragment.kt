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
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.timelinedetail.TimelineDetailActivity
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
        displayTabLayout()
        displayDateContainer()
        displayGraphContainer()
        displayPeriodContainer()
        displayRoadContextContainer()

        displayTimelineDetail()
        updateTimeline()

        viewModel.syncStatus.observe(this) {
            updateProgressVisibility(false)
            Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
        }

        viewModel.timelineDataLiveData.observe(this) {
            Toast.makeText(context, it.period.name, Toast.LENGTH_SHORT).show()
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
        TODO()
    }

    private fun displayGraphContainer() {
        TODO()
    }

    private fun displayDateContainer() {
        TODO()
    }

    private fun displayRoadContextContainer() {
        TODO()
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
}