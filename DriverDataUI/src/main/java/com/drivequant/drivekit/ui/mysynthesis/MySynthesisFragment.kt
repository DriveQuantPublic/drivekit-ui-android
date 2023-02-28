package com.drivequant.drivekit.ui.mysynthesis

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorView
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorView
import com.drivequant.drivekit.common.ui.component.scoreselector.DKScoreSelectorView
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKScoreTypeLevels
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
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
    private lateinit var legendTemporary: Button //TODO TEMPORARY

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
        this.legendTemporary = view.findViewById(R.id.button_legend_temp)

        checkViewModelInitialization()

        setupSwipeToRefresh()

        configureScoreSelectorView()
        configurePeriodSelector()
        configureDateSelector()
        configureScoreCard()

        // TODO temporary, for review. The code would be moved in Community Card part
        this.legendTemporary.setOnClickListener {
            configureLegendTemporary()
        }
        this.viewModel.updateData.observe(viewLifecycleOwner) {
            updatePeriodSelector()
            updateDateSelector()
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

    //TODO TEMPORARY
    private fun configureLegendTemporary() {
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(requireContext())
            .layout(R.layout.dk_my_synthesis_scores_legend_alert_dialog)
            .positiveButton(getString(R.string.dk_common_close)) { dialog, _ ->
                dialog.dismiss()
            }
            .cancelable(true)
            .show()

        alertDialog.findViewById<TextView>(R.id.my_synthesis_score_legend_title)?.apply {
            text = when (viewModel.selectedScore) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_safety_score
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_eco_score
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_distraction_score
                DKScoreType.SPEEDING -> R.string.dk_driverdata_speeding_score
            }.let {
                getString(it)
            }
            headLine2(DriveKitUI.colors.primaryColor())
        }

        alertDialog.findViewById<TextView>(R.id.my_synthesis_score_legend_description)?.apply {
            text = when (viewModel.selectedScore) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_score_info
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_score_info
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_score_info
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_score_info
            }.let {
                getString(it)
            }
            smallText()
        }

        alertDialog.findViewById<LinearLayout>(R.id.container_score_item)?.let { scoreItemContainer ->
            DKScoreTypeLevels.values().forEach {
                configureScoreItem(scoreItemContainer, viewModel.selectedScore, it)
            }
        }
    }

    private fun configureScoreItem(
        container: LinearLayout,
        dkScoreType: DKScoreType,
        scoreLevel: DKScoreTypeLevels)
    {
        val view = View.inflate(requireContext(), R.layout.dk_my_synthesis_scores_legend_item, null)
        view.findViewById<View>(R.id.score_color)?.let { scoreColor ->
            DrawableCompat.setTint(
                scoreColor.background,
                ContextCompat.getColor(scoreColor.context, scoreLevel.getColorResId())
            )
        }
        view.findViewById<TextView>(R.id.score_description)?.apply {
            val scoreValuesText: String = when (scoreLevel) {
                DKScoreTypeLevels.EXCELLENT -> R.string.dk_driverdata_mysynthesis_score_title_excellent
                DKScoreTypeLevels.VERY_GOOD -> R.string.dk_driverdata_mysynthesis_score_title_very_good
                DKScoreTypeLevels.GREAT -> R.string.dk_driverdata_mysynthesis_score_title_good
                DKScoreTypeLevels.MEDIUM -> R.string.dk_driverdata_mysynthesis_score_title_average
                DKScoreTypeLevels.NOT_GOOD -> R.string.dk_driverdata_mysynthesis_score_title_low
                DKScoreTypeLevels.BAD -> R.string.dk_driverdata_mysynthesis_score_title_bad
                DKScoreTypeLevels.VERY_BAD -> R.string.dk_driverdata_mysynthesis_score_title_very_bad
            }.let {
                getString(
                    it,
                    scoreLevel.getScoreLevels(dkScoreType).first.format(1),
                    scoreLevel.getScoreLevels(dkScoreType).second.format(1)
                )
            }
            this.text = DKSpannable().append(scoreValuesText, context.resSpans {
                color(DriveKitUI.colors.primaryColor())
                typeface(Typeface.NORMAL)
                size(R.dimen.dk_text_normal)
            }).space()
                .append(
                    getString(getScoreLevelDescription(dkScoreType, scoreLevel)),
                    context.resSpans {
                        color(DriveKitUI.colors.complementaryFontColor())
                        typeface(Typeface.NORMAL)
                        size(R.dimen.dk_text_small)
                    }).toSpannable()
        }
        container.addView(view)
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            params.leftMargin,
            view.resources.getDimension(R.dimen.dk_margin_half).toInt(),
            params.rightMargin,
            view.resources.getDimension(R.dimen.dk_margin_half).toInt()
        )
        view.layoutParams = params
    }

    //TODO viewmodel
    @StringRes
    private fun getScoreLevelDescription(dkScoreType: DKScoreType, scoreLevel: DKScoreTypeLevels) =
        when (scoreLevel) {
            DKScoreTypeLevels.EXCELLENT -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_excellent
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_excellent
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_excellent
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_excellent
            }
            DKScoreTypeLevels.VERY_GOOD -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_very_good
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_very_good
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_very_good
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_very_good
            }
            DKScoreTypeLevels.GREAT -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_good
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_good
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_good
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_good
            }
            DKScoreTypeLevels.MEDIUM -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_average
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_average
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_average
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_average
            }
            DKScoreTypeLevels.NOT_GOOD -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_low
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_low
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_low
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_low
            }
            DKScoreTypeLevels.BAD -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_bad
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_bad
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_bad
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_bad
            }
            DKScoreTypeLevels.VERY_BAD -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_very_bad
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_very_bad
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_very_bad
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_spedding_level_very_bad
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
