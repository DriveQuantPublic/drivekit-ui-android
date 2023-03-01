package com.drivequant.drivekit.ui.mysynthesis.component.scorecard

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.ui.R

internal class MySynthesisScoreCardView : LinearLayout {

    private lateinit var viewModel: MySynthesisScoreCardViewModel

    private lateinit var scoreCardView: LinearLayout
    private lateinit var title: TextView
    private lateinit var subTitle: TextView
    private lateinit var evolutionText: TextView
    private lateinit var trendIcon: ImageView

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
        scoreCardView = View.inflate(context, R.layout.dk_my_synthesis_score_card_view, null) as LinearLayout
        title = scoreCardView.findViewById(R.id.score_card_title)
        subTitle = scoreCardView.findViewById(R.id.score_card_subtitle)
        evolutionText = scoreCardView.findViewById(R.id.score_card_evolution_text)
        trendIcon = scoreCardView.findViewById(R.id.score_card_icon)

        scoreCardView.setDKStyle()
        addView(
            scoreCardView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun configure(viewModel: MySynthesisScoreCardViewModel) {
        this.viewModel = viewModel
        viewModel.onViewModelUpdated = this::update
        update()
    }

    private fun update() {
        configureTitle()
        configureCurrentScoreText(this.viewModel.score)
        configureEvolutionText(this.viewModel.previousScore)
        configureTrendIcon(this.viewModel.score, this.viewModel.previousScore)
    }

    private fun configureTitle() {
        title.apply {
            headLine2(DriveKitUI.colors.primaryColor())
            text = context.getString(viewModel.getCardTitleResId())
        }
    }

    private fun configureCurrentScoreText(scoreValue: Double?) {
        val subtitleTextColor = if (scoreValue != null) DriveKitUI.colors.primaryColor() else DriveKitUI.colors.complementaryFontColor()

        subTitle.apply {
            text = DKSpannable().computeScoreOnTen(scoreValue).toSpannable()
            highlightBig(subtitleTextColor)
        }
    }

    private fun configureEvolutionText(previousScore: Double?) {
        val evolutionText = viewModel.getEvolutionText(context)

        evolutionText.text = if(viewModel.hasScoredTrips() && viewModel.hasPreviousData()) {
            DKSpannable().append(context.getString(textResId)).space().computeScoreOnTen(previousScore).toSpannable()
        } else {
            context.getString(textResId)
        }
        evolutionText.normalText(DriveKitUI.colors.complementaryFontColor())
    }

    private fun configureTrendIcon(score: Double?, previousScore: Double?) {
        val iconColor =
            if (score != null && previousScore != null) DriveKitUI.colors.primaryColor()
            else DriveKitUI.colors.complementaryFontColor()

        ContextCompat.getDrawable(context, viewModel.getTrendIconResId())?.let { icon ->
            icon.tintDrawable(iconColor)
            trendIcon.setImageDrawable(icon)
        }
    }

    private fun DKSpannable.computeScoreOnTen(score: Double?): DKSpannable {
        val scoreText = (score?.format(1) ?: "-")
            .plus(" ")
            .plus(context.getString(R.string.dk_common_unit_score))
        return this.append(scoreText, context.resSpans { typeface(Typeface.BOLD) })
    }
}
