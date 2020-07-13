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
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.DriverProgression

/**
 * Created by Mohamed on 2020-05-13.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class DriverProgressionView : LinearLayout {

    private lateinit var textViewDriverProgression: TextView
    private lateinit var textViewLeaderBoardTitle: TextView
    private lateinit var imageViewLeaderBoard: ImageView
    private lateinit var imageViewDriverProgression: ImageView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_driver_progression_view, null).setDKStyle()
        textViewDriverProgression = view.findViewById(R.id.text_view_driver_progression)
        textViewLeaderBoardTitle = view.findViewById(R.id.text_view_ranking_title)
        imageViewLeaderBoard = view.findViewById(R.id.image_view_ranking_type)
        imageViewDriverProgression = view.findViewById(R.id.image_view_driver_position)
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
            imageViewLeaderBoard.visibility = View.GONE
            textViewLeaderBoardTitle.visibility = View.GONE
        }
        textViewLeaderBoardTitle.bigText()
        textViewDriverProgression.bigText()
    }

    fun setDriverProgression(
        leaderBoardStatus: SpannableString,
        driverProgression: DriverProgression,
        leaderBoardTitle: String,
        leaderBoardIcon: String) {
        textViewDriverProgression.text = leaderBoardStatus
        val rankingStatusDrawable = when (driverProgression) {
            DriverProgression.GOING_DOWN -> "dk_achievements_arrow_down"
            DriverProgression.GOING_UP -> "dk_achievements_arrow_up"
        }

        textViewLeaderBoardTitle.text = leaderBoardTitle

        DKResource.convertToDrawable(context, leaderBoardIcon)?.let {
            imageViewLeaderBoard.setImageDrawable(it)
        }

        rankingStatusDrawable.let {
            imageViewDriverProgression.setImageDrawable(DKResource.convertToDrawable(context, it))
        }
    }
}