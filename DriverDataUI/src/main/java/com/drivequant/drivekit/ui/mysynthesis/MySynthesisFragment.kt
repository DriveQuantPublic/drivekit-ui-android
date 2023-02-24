package com.drivequant.drivekit.ui.mysynthesis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorView
import com.drivequant.drivekit.common.ui.component.scoreselector.DKScoreSelectorView
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R

internal class MySynthesisFragment : Fragment() {

    companion object {
        fun newInstance() = MySynthesisFragment()
    }

    private lateinit var viewModel: MySynthesisViewModel
    private lateinit var scoreSelectorView: DKScoreSelectorView
    private lateinit var periodSelectorContainer: ViewGroup
    private lateinit var periodSelectorView: DKPeriodSelectorView
    private lateinit var scoreCardContainer: ViewGroup
    private lateinit var communityCardContainer: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_mysynthesis, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.scoreSelectorView = view.findViewById(R.id.scoreSelector)
        this.periodSelectorContainer = view.findViewById(R.id.period_selector_container)
        this.scoreCardContainer = view.findViewById(R.id.scoreCard_container)
        this.communityCardContainer = view.findViewById(R.id.communityCard_container)

        checkViewModelInitialization()

        setupSwipeToRefresh()

        configureScoreSelectorView()
        configurePeriodSelector()
    }

    override fun onResume() {
        super.onResume()
        tagScreen()
        checkViewModelInitialization()
    }

    private fun setupSwipeToRefresh() {
//        updateSwipeRefreshTripsVisibility(false)
//        dk_swipe_refresh_timeline.setOnRefreshListener {
//            updateTimeline()
//        }
    }

    private fun configureScoreSelectorView() {
        this.scoreSelectorView.configure(this.viewModel.scoreSelectorViewModel)
        this.scoreSelectorView.visibility = if (this.scoreSelectorView.scoreCount() < 2) View.GONE else View.VISIBLE
    }

    private fun configurePeriodSelector() {
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

    private fun tagScreen() {
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_my_synthesis"
            ), javaClass.simpleName
        )
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            activity?.application?.let { application ->
                if (!this::viewModel.isInitialized) {
                    viewModel = ViewModelProvider(
                        this,
                        MySynthesisViewModel.MySynthesisViewModelFactory(application)
                    )[MySynthesisViewModel::class.java]
                }
            }
        }
    }

}
