package com.drivequant.drivekit.driverachievement.ui.leaderboard.commons.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
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
               val params = right_container.layoutParams as LayoutParams
               params.weight = 1.7f
               right_container.layoutParams = params
               left_container.visibility = View.INVISIBLE
        }
        text_view_header_title.bigText()
        text_view_global_rank.bigText()
    }

    fun setHeaderData(rankingViewModel: RankingViewModel) {
        text_view_global_rank.text = rankingViewModel.rankingHeaderData.getDriverGlobalRank(context)
        val progressionIconId = when (rankingViewModel.rankingHeaderData.getProgression(rankingViewModel.previousRank)) {
            DriverProgression.GOING_DOWN -> "dk_achievements_arrow_down"
            DriverProgression.GOING_UP -> "dk_achievements_arrow_up"
        }
        text_view_header_title.text = rankingViewModel.rankingHeaderData.getTitle()
        DKResource.convertToDrawable(context, rankingViewModel.rankingHeaderData.getIcon())?.let {
            image_view_ranking_type.setImageDrawable(it)
        }
        progressionIconId.let {
            image_view_driver_progression.setImageDrawable(DKResource.convertToDrawable(context, it))
        }
    }
}