package com.drivequant.drivekit.ui.mysynthesis.component.communitycard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.ui.R

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
}