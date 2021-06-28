package com.drivequant.drivekit.common.ui.component.ranking.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.RankingHeaderDisplayType
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DKRankingViewModel
import com.drivequant.drivekit.common.ui.extension.bigText

import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_ranking_header_view.view.*

/**
 * Created by Mohamed on 2020-07-03.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class RankingHeaderView : LinearLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_ranking_header_view, null)
        addView(view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
        setStyle()
    }

    private fun setStyle() {
        val params = container.layoutParams as MarginLayoutParams
        params.setMargins(params.leftMargin, context.resources.getDimension(R.dimen.dk_margin_medium).toInt(), params.rightMargin, context.resources.getDimension(R.dimen.dk_margin_medium).toInt())
        layoutParams = params
        text_view_header_title.bigText()
    }

    fun setHeaderData(rankingViewModel: DKRankingViewModel) {
        container.setBackgroundColor(rankingViewModel.getBackgroundColor())
        driver_progression.setDriverProgression(rankingViewModel)
        text_view_header_title.text = DKResource.convertToString(context, rankingViewModel.getTitle())
        rankingViewModel.getIcon(context)?.let {
            image_view_ranking_type.setImageDrawable(it)
        }
        if (rankingViewModel.getHeaderDisplayType() == RankingHeaderDisplayType.COMPACT) {
            full_ranking_header_container.visibility = View.GONE
            driver_progression_only.visibility = View.VISIBLE
            driver_progression_only.setDriverProgression(rankingViewModel)
        }
    }
}