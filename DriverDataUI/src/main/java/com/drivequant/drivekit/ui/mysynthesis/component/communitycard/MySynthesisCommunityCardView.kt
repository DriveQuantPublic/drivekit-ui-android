package com.drivequant.drivekit.ui.mysynthesis.component.communitycard

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
        communityCardView = View.inflate(context, R.layout.dk_my_synthesis_community_card_view, null) as LinearLayout
        title = communityCardView.findViewById(R.id.community_card_title)

        communityCardView.setDKStyle()
        addView(
            communityCardView, ViewGroup.LayoutParams(
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
    }

    private fun configureTitle() {
        title.apply {
            text = viewModel.getTitleText(context)
            //TODO style
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