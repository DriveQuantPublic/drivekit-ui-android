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
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.dateselector.DateSelectorView
import com.drivequant.drivekit.timeline.ui.component.periodselector.PeriodSelectorView
import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextView

class TimelineDetailFragment : Fragment() {

    private lateinit var viewModel: TimelineDetailViewModel

    private lateinit var periodSelectorContainer: LinearLayout
    private lateinit var periodSelectorView: PeriodSelectorView

    private lateinit var dateSelectorContainer: LinearLayout
    private lateinit var dateSelectorView: DateSelectorView

    private lateinit var roadContextContainer: LinearLayout
    private lateinit var roadContextView: RoadContextView

    private lateinit var graphContainer: LinearLayout
    //private val graphView: List<TimelineGraphView>

    private lateinit var nestedScrollView: NestedScrollView

    companion object {
        fun newInstance() = TimelineDetailFragment()
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

        /*nestedScrollView.setOnTouchListener { _, motionEvent ->
            //graphView.manageTouchEvent(motionEvent)
        }*/

        initViewModel()

        /*viewModel.clearObserver.observe(this) {
            activity?.onBackPressed()
        }*/
    }

    private fun initViewModel() {
        if (!this::viewModel.isInitialized) {
            activity?.application?.let { application ->
                if (!this::viewModel.isInitialized) {
                    // TODO
                    /*viewModel = ViewModelProviders.of(
                        this,
                        TimelineDetailViewModel.TimelineDetailViewModelFactory(application)
                    ).get(TimelineDetailViewModel::class.java)*/
                }
            }
        }
    }
}
