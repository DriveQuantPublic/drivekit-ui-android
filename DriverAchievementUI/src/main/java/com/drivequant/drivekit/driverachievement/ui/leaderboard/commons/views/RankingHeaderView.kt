package com.drivequant.drivekit.driverachievement.ui.leaderboard.commons.views

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
import com.drivequant.drivekit.driverachievement.ui.leaderboard.extension.setMarginBottom
import com.drivequant.drivekit.driverachievement.ui.leaderboard.extension.setMarginTop
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.DriverProgression
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingViewModel
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
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setStyle()
    }

    private fun setStyle() {
        if (DriverAchievementUI.rankingTypes.size > 1) {
            image_view_ranking_type.visibility = View.GONE
            text_view_header_title.visibility = View.GONE
        }
        container.setMarginTop(context.resources.getDimension(R.dimen.dk_margin_medium).toInt())
        container.setMarginBottom(context.resources.getDimension(R.dimen.dk_margin_medium).toInt())
        text_view_header_title.bigText()
        text_view_global_rank.bigText()
    }

    fun setHeaderData(rankingViewModel: RankingViewModel) {
        text_view_global_rank.text = rankingViewModel.rankingHeaderData.getDriverGlobalRank(context)
        text_view_header_title.text = rankingViewModel.rankingHeaderData.getTitle()
        val progressionIconId =
            when (rankingViewModel.rankingHeaderData.getProgression(rankingViewModel.previousRank)) {
                DriverProgression.GOING_DOWN -> "dk_achievements_arrow_down"
                DriverProgression.GOING_UP -> "dk_achievements_arrow_up"
            }

        image_view_driver_progression.visibility =
        if (rankingViewModel.syncStatus == RankingSyncStatus.USER_NOT_RANKED) {
            View.GONE
        } else {
            View.VISIBLE
        }
        DKResource.convertToDrawable(context, rankingViewModel.rankingHeaderData.getIcon())?.let {
            image_view_ranking_type.setImageDrawable(it)
        }
        progressionIconId.let {
            image_view_driver_progression.setImageDrawable(
                DKResource.convertToDrawable(
                    context,
                    it
                )
            )
        }
    }
}