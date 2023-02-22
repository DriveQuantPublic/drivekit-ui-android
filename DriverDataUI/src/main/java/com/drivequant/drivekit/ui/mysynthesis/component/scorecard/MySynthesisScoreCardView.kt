package com.drivequant.drivekit.ui.mysynthesis.component.scorecard

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.common.DKPeriod
import com.drivequant.drivekit.driverdata.timeline.DKScoreEvolutionTrend
import com.drivequant.drivekit.ui.R
import kotlinx.android.synthetic.main.dk_my_synthesis_score_card_view.view.*

// todo internal ?
class MySynthesisScoreCardView : LinearLayout {

    private lateinit var viewModel: MySynthesisScoreCardViewModel

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
        val view = View.inflate(context, R.layout.dk_my_synthesis_score_card_view, null).setDKStyle()
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        setStyle()
    }

    fun configure(viewModel: MySynthesisScoreCardViewModel) {
        this.viewModel = viewModel
        update()
    }

    private fun setStyle() {
        (my_synthesis_score_card_view?.layoutParams as MarginLayoutParams?)?.let { params ->
            params.setMargins(
                context.resources.getDimension(R.dimen.dk_margin_quarter).toInt(),
                params.topMargin,
                context.resources.getDimension(R.dimen.dk_margin_quarter).toInt(),
                params.bottomMargin
            )
            layoutParams = params
        }
    }


    private fun update() {
        score_card_title.apply {
            headLine2(DriveKitUI.colors.primaryColor())
            score_card_title.text = context.getString(viewModel.getCardTitleResId())
        }

        this.viewModel.scoreSynthesis?.let { scoreSynthesis ->

            //TODO cut in separate methods
            val scoreValue = scoreSynthesis.scoreValue
            val subtitleTextColor = if (scoreValue != null) DriveKitUI.colors.primaryColor() else DriveKitUI.colors.complementaryFontColor()

            score_card_subtitle.text = DKSpannable().computeScoreOnTen(scoreValue).toSpannable()
            score_card_subtitle.highlightBig(subtitleTextColor)

            val previousScore = scoreSynthesis.previousScoreValue

            score_card_evolution_text.normalText()
            if (previousScore != null) { // has previous score
                when (this.viewModel.selectedPeriod) {
                    DKPeriod.WEEK -> R.string.dk_driverdata_mysynthesis_previous_week
                    DKPeriod.MONTH -> R.string.dk_driverdata_mysynthesis_previous_month
                    DKPeriod.YEAR -> R.string.dk_driverdata_mysynthesis_previous_year
                }.let {
                    score_card_evolution_text.text = DKSpannable().append(context.getString(it)).space().computeScoreOnTen(previousScore).toSpannable()
                }
            } else {
                // TODO no previous score
            }
            score_card_evolution_text.normalText(DriveKitUI.colors.complementaryFontColor())

            var drawableResId: Int = R.drawable.dk_driver_data_trend_steady
            val iconColor = if (scoreValue != null && previousScore != null) DriveKitUI.colors.primaryColor() else DriveKitUI.colors.complementaryFontColor()
            scoreSynthesis.evolutionTrend?.let { trend ->
                when (trend) {
                    DKScoreEvolutionTrend.UP -> R.drawable.dk_driver_data_trend_positive
                    DKScoreEvolutionTrend.DOWN -> R.drawable.dk_driver_data_trend_negative
                    DKScoreEvolutionTrend.SAME -> R.drawable.dk_driver_data_trend_steady
                }.let {
                    drawableResId = it
                }
            }
            val drawable = ContextCompat.getDrawable(context, drawableResId)
            drawable?.let { icon ->
                icon.tintDrawable(iconColor)
                score_card_icon.setImageDrawable(icon)
            }
        }
    }

    private fun DKSpannable.computeScoreOnTen(score: Double?): DKSpannable {
        val scoreText = (score?.format(1) ?: "-")
            .plus(" ")
            .plus(context.getString(R.string.dk_common_unit_score))
        return this.append(scoreText, context.resSpans { typeface(Typeface.BOLD) })
    }
}