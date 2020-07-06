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
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.DriverProgression

/**
 * Created by Mohamed on 2020-05-13.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class UserProgressionView : LinearLayout {

    private lateinit var imageViewRankingStatus: ImageView
    private lateinit var textViewRankingStatus: TextView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_user_progression_view, null).setDKStyle()
        imageViewRankingStatus = view.findViewById(R.id.image_view_user_progression)
        textViewRankingStatus = view.findViewById(R.id.text_view_user_progression)
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

    fun setUserProgression(leaderBoardStatus: SpannableString, driverProgression: DriverProgression) {
        textViewRankingStatus.text = leaderBoardStatus
        val rankingStatusDrawable = when (driverProgression) {
            DriverProgression.GOING_DOWN -> "dk_achievements_arrow_down"
            DriverProgression.GOING_UP -> "dk_achievements_arrow_up"
            DriverProgression.STEADY -> null
        }

        rankingStatusDrawable?.let {
            imageViewRankingStatus.setImageDrawable(DKResource.convertToDrawable(context, it))
        } ?: run {
            imageViewRankingStatus.visibility = GONE
        }
    }
}