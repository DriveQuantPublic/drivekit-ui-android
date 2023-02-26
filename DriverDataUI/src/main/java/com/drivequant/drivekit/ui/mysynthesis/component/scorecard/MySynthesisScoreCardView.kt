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
import com.drivequant.drivekit.driverdata.timeline.DKScoreSynthesis
import com.drivequant.drivekit.ui.R
import kotlinx.android.synthetic.main.dk_my_synthesis_score_card_view.view.*

// TODO restore internal
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
        viewModel.onViewModelUpdated = this::update
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
        val scoreValue = this.viewModel.scoreSynthesis?.scoreValue
        val previousScore = this.viewModel.scoreSynthesis?.previousScoreValue
        configureTitle()
        configureCurrentScoreText(scoreValue)
        configureEvolutionText(previousScore)
        configureTrendIcon(this.viewModel.scoreSynthesis)
    }

    private fun configureTitle() {
        score_card_title.apply {
            headLine2(DriveKitUI.colors.primaryColor())
            score_card_title.text = context.getString(viewModel.getCardTitleResId())
        }
    }

    private fun configureCurrentScoreText(scoreValue: Double?) {
        val subtitleTextColor = if (scoreValue != null) DriveKitUI.colors.primaryColor() else DriveKitUI.colors.complementaryFontColor()

        score_card_subtitle.text = DKSpannable().computeScoreOnTen(scoreValue).toSpannable()
        score_card_subtitle.highlightBig(subtitleTextColor)
    }

    private fun configureEvolutionText(previousScore: Double?) {
        val textResId = viewModel.getEvolutionTextResId()

        score_card_evolution_text.text = if(viewModel.hasScoredTrips() && viewModel.hasPreviousData()) {
            DKSpannable().append(context.getString(textResId)).space().computeScoreOnTen(previousScore).toSpannable()
        } else {
            context.getString(textResId)
        }
        score_card_evolution_text.normalText(DriveKitUI.colors.complementaryFontColor())
    }

    private fun configureTrendIcon(scoreSynthesis: DKScoreSynthesis?) {
        val iconColor =
            if (scoreSynthesis?.scoreValue != null && scoreSynthesis.previousScoreValue != null) DriveKitUI.colors.primaryColor()
            else DriveKitUI.colors.complementaryFontColor()

        ContextCompat.getDrawable(context, viewModel.getTrendIconResId())?.let { icon ->
            icon.tintDrawable(iconColor)
            score_card_icon.setImageDrawable(icon)
        }
    }

    private fun DKSpannable.computeScoreOnTen(score: Double?): DKSpannable {
        val scoreText = (score?.format(1) ?: "-")
            .plus(" ")
            .plus(context.getString(R.string.dk_common_unit_score))
        return this.append(scoreText, context.resSpans { typeface(Typeface.BOLD) })
    }
}
