package com.drivequant.drivekit.ui.mysynthesis.component.scorecard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.ui.R
import kotlinx.android.synthetic.main.dk_my_synthesis_score_card_view.view.*

internal class MySynthesisScoreCardView : LinearLayout {

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
        score_card_title.headLine2(DriveKitUI.colors.primaryColor())
        score_card_subtitle.highlightBig(DriveKitUI.colors.primaryColor())
        score_card_evolution_text.normalText(DriveKitUI.colors.complementaryFontColor())
    }
}