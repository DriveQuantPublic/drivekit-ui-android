package com.drivequant.drivekit.ui.mysynthesis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorView
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorView
import com.drivequant.drivekit.common.ui.component.scoreselector.DKScoreSelectorView
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.mysynthesis.component.scorecard.MySynthesisScoreCardView

internal class MySynthesisFragment : Fragment() {

    companion object {
        fun newInstance() = MySynthesisFragment()
    }

    private lateinit var viewModel: MySynthesisViewModel
    private lateinit var scoreSelectorView: DKScoreSelectorView
    private lateinit var periodSelectorContainer: ViewGroup
    private lateinit var periodSelectorView: DKPeriodSelectorView
    private lateinit var dateSelectorContainer: ViewGroup
    private lateinit var dateSelectorView: DKDateSelectorView
    private lateinit var scoreCardView: MySynthesisScoreCardView
    private lateinit var communityCardContainer: ViewGroup
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_mysynthesis, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.scoreSelectorView = view.findViewById(R.id.scoreSelector)
        this.periodSelectorContainer = view.findViewById(R.id.period_selector_container)
        this.dateSelectorContainer = view.findViewById(R.id.date_selector_container)
        this.scoreCardView = view.findViewById(R.id.scoreCard)
        this.communityCardContainer = view.findViewById(R.id.communityCard_container)
        this.swipeRefreshLayout = view.findViewById(R.id.dk_swipe_refresh_mysynthesis)

        checkViewModelInitialization()

        setupSwipeToRefresh()

        configureScoreSelectorView()
        configurePeriodSelector()
        configureDateSelector()
        configureScoreCard()


        this.viewModel.updateData.observe(viewLifecycleOwner) {
            updatePeriodSelector()
            updateDateSelector()
            configureScoreCard()
        }
        this.viewModel.syncStatus.observe(viewLifecycleOwner) {
            updateSwipeRefreshTripsVisibility(false)
        }
        updateData()
    }

    override fun onResume() {
        super.onResume()
        tagScreen()
        checkViewModelInitialization()
    }

    private fun setupSwipeToRefresh() {
        updateSwipeRefreshTripsVisibility(false)
        this.swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
    }

    private fun updateData() {
        updateSwipeRefreshTripsVisibility(true)
        this.viewModel.updateData()
    }

    private fun updatePeriodSelector() {
        this.periodSelectorView.configure(this.viewModel.periodSelectorViewModel)
    }

    private fun updateDateSelector() {
        if (this.viewModel.dateSelectorViewModel.hasDates()) {
            this.dateSelectorContainer.visibility = View.VISIBLE
            this.dateSelectorView.configure(viewModel.dateSelectorViewModel)
        } else {
            this.dateSelectorContainer.visibility = View.GONE
        }
    }

    private fun updateSwipeRefreshTripsVisibility(display: Boolean) {
        this.swipeRefreshLayout.isRefreshing = display
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

    private fun configureDateSelector() {
        context?.let {
            dateSelectorView = DKDateSelectorView(it)
            dateSelectorView.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                configure(viewModel.dateSelectorViewModel)
            }
            dateSelectorContainer.apply {
                removeAllViews()
                addView(dateSelectorView)
            }
        }
    }

    private fun configureScoreCard() {
        this.scoreCardView.configure(viewModel.scoreCardViewModel)
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
