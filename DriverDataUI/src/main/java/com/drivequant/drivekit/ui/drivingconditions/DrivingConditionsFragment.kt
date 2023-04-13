package com.drivequant.drivekit.ui.drivingconditions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorView
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorView
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.drivingconditions.component.summary.DrivingConditionsSummaryCardView
import java.util.*

internal class DrivingConditionsFragment : Fragment() {

    companion object {
        const val SELECTED_PERIOD_ID_EXTRA = "selectedPeriod"
        const val SELECTED_DATE_ID_EXTRA = "selectedDate"

        fun newInstance(
            selectedPeriod: DKPeriod?,
            selectedDate: Date?
        ) = DrivingConditionsFragment().also {
            it.initialSelectedPeriod = selectedPeriod
            it.initialSelectedDate = selectedDate
        }
    }

    val selectedPeriod: DKPeriod
        get() = this.viewModel.periodSelectorViewModel.selectedPeriod
    val selectedDate: Date?
        get() = this.viewModel.selectedDate
    private lateinit var viewModel: DrivingConditionsViewModel
    private lateinit var periodSelectorContainer: ViewGroup
    private lateinit var periodSelectorView: DKPeriodSelectorView
    private lateinit var dateSelectorContainer: ViewGroup
    private lateinit var dateSelectorView: DKDateSelectorView
    private lateinit var summaryCardView: DrivingConditionsSummaryCardView
    private lateinit var drivingConditionsContainer: ViewGroup
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var initialSelectedPeriod: DKPeriod? = null
    private var initialSelectedDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_drivingconditions, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.periodSelectorContainer = view.findViewById(R.id.period_selector_container)
        this.dateSelectorContainer = view.findViewById(R.id.date_selector_container)
        this.summaryCardView = view.findViewById(R.id.driving_conditions_summary)

        this.drivingConditionsContainer = view.findViewById(R.id.driving_conditions_container)
        this.swipeRefreshLayout = view.findViewById(R.id.dk_swipe_refresh_drivingconditions)

        savedInstanceState?.let {
            it.getString(SELECTED_PERIOD_ID_EXTRA)?.let { savedPeriod ->
                this.initialSelectedPeriod = DKPeriod.valueOf(savedPeriod)
            }
            val savedDate = it.getLong(SELECTED_DATE_ID_EXTRA, 0L)
            if (savedDate > 0) {
                val date = Date(savedDate)
                this.initialSelectedDate = date
            }
        }

        checkViewModelInitialization()

        setupSwipeToRefresh()

        configurePeriodSelector()
        configureDateSelector()
        configureSummaryCard()

        this.viewModel.updateData.observe(viewLifecycleOwner) {
            updatePeriodSelector()
            updateDateSelector()
            configureSummaryCard()
        }
        this.viewModel.syncStatus.observe(viewLifecycleOwner) {
            updateSwipeRefreshTripsVisibility(false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SELECTED_PERIOD_ID_EXTRA, selectedPeriod.name)
        this.selectedDate?.let { outState.putLong(SELECTED_DATE_ID_EXTRA, it.time) }
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

    private fun configureSummaryCard() {
        context?.let {
            this.summaryCardView.configure(this.viewModel.summaryCardViewModel)
        }
    }

    private fun tagScreen() {
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_driving_conditions"
            ), javaClass.simpleName
        )
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            activity?.application?.let { application ->
                if (!this::viewModel.isInitialized) {
                    viewModel = ViewModelProvider(
                        this,
                        DrivingConditionsViewModel.DrivingConditionsViewModelFactory(
                            application, this.initialSelectedPeriod, this.initialSelectedDate
                        )
                    )[DrivingConditionsViewModel::class.java]
                }
            }
        }
    }
}
