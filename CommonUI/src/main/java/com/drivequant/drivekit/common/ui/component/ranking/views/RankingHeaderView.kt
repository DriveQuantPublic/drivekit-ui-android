package com.drivequant.drivekit.common.ui.component.ranking.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.RankingHeaderDisplayType
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DKRankingViewModel
import com.drivequant.drivekit.common.ui.extension.bigText

/**
 * Created by Mohamed on 2020-07-03.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class RankingHeaderView : LinearLayout {

    private lateinit var container: View
    private lateinit var headerTitleView: TextView
    private lateinit var driverProgressionView: DriverProgressionView
    private lateinit var rankingTypeImageView: ImageView
    private lateinit var driverProgressionOnlyView: DriverProgressionView
    private lateinit var fullRankingHeaderContainer: View

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

        this.container = view.findViewById(R.id.container)
        this.headerTitleView = view.findViewById(R.id.text_view_header_title)
        this.driverProgressionView = view.findViewById(R.id.driver_progression)
        this.rankingTypeImageView = view.findViewById(R.id.image_view_ranking_type)
        this.driverProgressionOnlyView = view.findViewById(R.id.driver_progression_only)
        this.fullRankingHeaderContainer = view.findViewById(R.id.full_ranking_header_container)

        setStyle()
    }

    private fun setStyle() {
        val params = container.layoutParams as MarginLayoutParams
        params.setMargins(params.leftMargin, context.resources.getDimension(R.dimen.dk_margin_medium).toInt(), params.rightMargin, context.resources.getDimension(R.dimen.dk_margin_medium).toInt())
        layoutParams = params
        this.headerTitleView.bigText()
    }

    fun setHeaderData(rankingViewModel: DKRankingViewModel) {
        this.container.setBackgroundColor(rankingViewModel.getBackgroundColor())
        this.driverProgressionView.setDriverProgression(rankingViewModel)
        this.headerTitleView.setText(rankingViewModel.getTitleResId())
        rankingViewModel.getIcon(context)?.let {
            this.rankingTypeImageView.setImageDrawable(it)
        }
        if (rankingViewModel.getHeaderDisplayType() == RankingHeaderDisplayType.COMPACT) {
            this.fullRankingHeaderContainer.visibility = View.GONE
            this.driverProgressionOnlyView.visibility = View.VISIBLE
            this.driverProgressionOnlyView.setDriverProgression(rankingViewModel)
        }
    }
}
