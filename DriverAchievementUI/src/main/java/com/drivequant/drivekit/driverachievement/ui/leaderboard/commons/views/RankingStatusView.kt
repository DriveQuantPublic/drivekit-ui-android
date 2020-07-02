package com.drivequant.drivekit.driverachievement.ui.leaderboard.commons.views

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingStatus

/**
 * Created by Mohamed on 2020-05-13.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class RankingStatusView : LinearLayout {

    private lateinit var imageViewRankingStatus: ImageView
    private lateinit var textViewRankingStatus: TextView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_ranking_status_view, null).setDKStyle()
        imageViewRankingStatus = view.findViewById(R.id.image_view_ranking_status)
        textViewRankingStatus = view.findViewById(R.id.text_view_ranking_status)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setStyle()
    }

    private fun setStyle() {
        textViewRankingStatus.bigText()

    }

    fun setRankingStatus(leaderBoardStatus: SpannableString, rankingStatus: RankingStatus) {
        textViewRankingStatus.text = leaderBoardStatus
        val rankingStatusDrawable = when (rankingStatus) {
            RankingStatus.GOING_DOWN -> "dk_achievements_arrow_down"
            RankingStatus.GOING_UP -> "dk_achievements_arrow_up"
            RankingStatus.STEADY -> null
        }

        rankingStatusDrawable?.let {
            imageViewRankingStatus.setImageDrawable(DKResource.convertToDrawable(context, it))
        } ?: run {
            imageViewRankingStatus.visibility = GONE
        }
    }
}