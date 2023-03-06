package com.drivequant.drivekit.ui.mysynthesis.component.communitycard

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKScoreTypeLevel
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.getScoreLevelDescription

internal class MySynthesisCommunityCardView : LinearLayout {

    private lateinit var viewModel: MySynthesisCommunityCardViewModel

    private lateinit var communityCardView: LinearLayout
    private lateinit var title: TextView

    private lateinit var communityStats: ConstraintLayout
    private lateinit var communityStatsTitle: TextView
    private lateinit var communityStatsTrips: TextView
    private lateinit var communityStatsDistance: TextView
    private lateinit var communityStatsDriversCount: TextView

    private lateinit var driverStats: ConstraintLayout
    private lateinit var driverStatsTitle: TextView
    private lateinit var driverStatsTrips: TextView
    private lateinit var driverStatsDistance: TextView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context, attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        this.communityCardView = View.inflate(context, R.layout.dk_my_synthesis_community_card_view, null) as LinearLayout
        this.title = communityCardView.findViewById(R.id.community_card_title)

        this.communityStats = this.communityCardView.findViewById(R.id.community_stats)
        this.communityStatsTitle = this.communityStats.findViewById(R.id.title)
        this.communityStatsTitle.apply {
            text = context.getString(R.string.dk_driverdata_mysynthesis_my_community)
            smallText(DriveKitUI.colors.mainFontColor())
        }
        this.communityStatsTrips = this.communityStats.findViewById(R.id.trips_count_title)
        this.communityStatsDistance = this.communityStats.findViewById(R.id.distance_count_title)
        this.communityStatsDriversCount = this.communityStats.findViewById(R.id.drivers_count_title)

        this.communityCardView.setDKStyle()
        addView(
            this.communityCardView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun configure(viewModel: MySynthesisCommunityCardViewModel) {
        this.viewModel = viewModel
        viewModel.onViewModelUpdated = this::update
        update()
    }

    private fun update() {
        configureTitle()
        configureCommunityInfos()
    }

    private fun configureTitle() {
        title.apply {
            text = viewModel.getTitleText(context)
            headLine2(viewModel.getTitleColor())
        }
    }

    private fun configureCommunityInfos() {
        this.communityStatsTrips.apply {
            text = viewModel.getCommunityTripsText(context)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
        this.communityStatsDistance.apply {
            text = viewModel.getCommunityDistanceText(context)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
        this.communityStatsDriversCount.apply {
            text = viewModel.getCommunityActiveDriversText(context)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
    }

    private fun showScoreLegend() {
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(context)
            .layout(R.layout.dk_my_synthesis_scores_legend_alert_dialog)
            .positiveButton(context.getString(R.string.dk_common_close)) { dialog, _ ->
                dialog.dismiss()
            }
            .cancelable(true)
            .show()

        alertDialog.findViewById<TextView>(R.id.my_synthesis_score_legend_title)?.apply {
            text = context.getString(viewModel.getLegendTitle())
            headLine2(DriveKitUI.colors.primaryColor())
        }

        alertDialog.findViewById<TextView>(R.id.my_synthesis_score_legend_description)?.apply {
            text = context.getString(viewModel.getLegendDescription())
            smallText()
        }

        alertDialog.findViewById<LinearLayout>(R.id.container_score_item)?.let { scoreItemContainer ->
            DKScoreTypeLevel.values().forEach {
                configureLegendScoreItem(scoreItemContainer, viewModel.selectedScoreType, it)
            }
        }
    }

    private fun configureLegendScoreItem(
        container: LinearLayout,
        dkScoreType: DKScoreType,
        scoreLevel: DKScoreTypeLevel
    ) {
        val view = View.inflate(context, R.layout.dk_my_synthesis_scores_legend_item, null)
        view.findViewById<View>(R.id.score_color)?.let { scoreColor ->
            DrawableCompat.setTint(
                scoreColor.background,
                ContextCompat.getColor(scoreColor.context, scoreLevel.getColorResId())
            )
        }
        view.findViewById<TextView>(R.id.score_description)?.apply {
            val scoreLevels = scoreLevel.getScoreLevel(dkScoreType)
            val scoreValuesText: String = viewModel.getLegendScoreAppreciationTitle(scoreLevel).let {
                context.getString(
                    it,
                    scoreLevels.first.format(1),
                    scoreLevels.second.format(1)
                )
            }
            this.text = DKSpannable().append(scoreValuesText, context.resSpans {
                color(DriveKitUI.colors.primaryColor())
                typeface(Typeface.NORMAL)
                size(R.dimen.dk_text_normal)
            }).space()
                .append(
                    context.getString(scoreLevel.getScoreLevelDescription(dkScoreType)),
                    context.resSpans {
                        color(DriveKitUI.colors.complementaryFontColor())
                        typeface(Typeface.NORMAL)
                        size(R.dimen.dk_text_small)
                    }).toSpannable()
        }
        container.addView(view)
        val params = view.layoutParams as MarginLayoutParams
        params.setMargins(
            params.leftMargin,
            view.resources.getDimension(R.dimen.dk_margin_half).toInt(),
            params.rightMargin,
            view.resources.getDimension(R.dimen.dk_margin_half).toInt()
        )
        view.layoutParams = params
    }
}