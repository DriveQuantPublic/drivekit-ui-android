package com.drivequant.drivekit.driverachievement.ui.rankings.commons.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.extension.bigText

import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.common.ui.extension.setMarginBottom
import com.drivequant.drivekit.common.ui.extension.setMarginTop
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.DriverProgression
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingViewModel
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
        val view = View.inflate(context, R.layout.dk_ranking_header_view, null).setDKStyle()
        addView(view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
        setStyle()
    }

    private fun setStyle() {
        text_view_header_title.bigText()
        container.setMarginTop(context.resources.getDimension(R.dimen.dk_margin_medium).toInt())
        container.setMarginBottom(context.resources.getDimension(R.dimen.dk_margin_medium).toInt())

    }

    fun setHeaderData(rankingViewModel: RankingViewModel) {
        driver_progression.setDriverProgression(rankingViewModel)
        text_view_header_title.text = DKResource.convertToString(context, rankingViewModel.rankingHeaderData.getTitle())


        DKResource.convertToDrawable(context, rankingViewModel.rankingHeaderData.getIcon())?.let {
            image_view_ranking_type.setImageDrawable(it)
        }

        if (DriverAchievementUI.rankingTypes.size > 1 && rankingViewModel.rankingSelectorsData.size > 1) {
            full_ranking_header_container.visibility = View.GONE
            driver_progression_only.visibility = View.VISIBLE
            driver_progression_only.setDriverProgression(rankingViewModel)
        }
    }
}