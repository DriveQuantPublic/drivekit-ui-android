package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.contextcard.view.DKContextCardView
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorView
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorView
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.timeline.ui.DispatchTouchLinearLayout
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.graph.view.TimelineGraphView
import java.util.Date

internal class TimelineDetailFragment : Fragment() {

    internal lateinit var viewModel: TimelineDetailViewModel
        private set

    private lateinit var dispatchTouchLinearLayout: DispatchTouchLinearLayout
    private lateinit var selectedScore: DKScoreType
    private lateinit var selectedPeriod: DKPeriod
    private lateinit var selectedDate: Date

    private lateinit var periodSelectorContainer: LinearLayout
    private lateinit var periodSelectorView: DKPeriodSelectorView

    private lateinit var dateSelectorContainer: LinearLayout
    private lateinit var dateSelectorView: DKDateSelectorView

    private lateinit var roadContextContainer: LinearLayout
    private lateinit var contextCardView: DKContextCardView

    private lateinit var graphContainer: LinearLayout

    companion object {
        private const val SELECTED_SCORE_ID_EXTRA = "selectedScore"
        private const val SELECTED_PERIOD_ID_EXTRA = "selectedPeriod"
        private const val SELECTED_DATE_ID_EXTRA = "selectedDate"

        fun newInstance(
            selectedScore: DKScoreType,
            selectedPeriod: DKPeriod,
            selectedDate: Date
        ): TimelineDetailFragment {
            val fragment = TimelineDetailFragment()
            fragment.selectedScore = selectedScore
            fragment.selectedPeriod = selectedPeriod
            fragment.selectedDate = selectedDate
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_timeline_detail, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dispatchTouchLinearLayout = view.findViewById(R.id.dispatch_touch_linear_layout)
        periodSelectorContainer = view.findViewById(R.id.period_selector_container)
        dateSelectorContainer = view.findViewById(R.id.date_selector_container)
        roadContextContainer = view.findViewById(R.id.road_context_container)
        graphContainer = view.findViewById(R.id.graph_container)

        savedInstanceState?.let {
            it.getString(SELECTED_SCORE_ID_EXTRA)?.let { savedScore ->
                selectedScore = DKScoreType.valueOf(savedScore)
            }
            it.getString(SELECTED_PERIOD_ID_EXTRA)?.let { savedPeriod ->
                selectedPeriod = DKPeriod.valueOf(savedPeriod)
            }
            val savedDate = it.getLong(SELECTED_DATE_ID_EXTRA)
            if (savedDate > 0) {
                val date = Date()
                date.time = savedDate
                selectedDate = date
            }
        }

        initViewModel()

        (activity as? androidx.appcompat.app.AppCompatActivity)?.setActivityTitle(getString(viewModel.titleId))

        viewModel.updateData.observe(viewLifecycleOwner) {
            periodSelectorView.configure(viewModel.periodSelectorViewModel)
            contextCardView.configure(viewModel.roadContextViewModel)
            dateSelectorView.configure(viewModel.dateSelectorViewModel)

            configureGraphContainer()
        }

        configurePeriodContainer()
        configureDateContainer()
        configureRoadContextContainer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SELECTED_SCORE_ID_EXTRA, selectedScore.name)
        outState.putString(SELECTED_PERIOD_ID_EXTRA, selectedPeriod.name)
        outState.putLong(SELECTED_DATE_ID_EXTRA, selectedDate.time)
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
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_timeline_score_detail), javaClass.simpleName)
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
                            selectedDate
                        )
                    )[TimelineDetailViewModel::class.java]
                }
            }
        }
    }
}
