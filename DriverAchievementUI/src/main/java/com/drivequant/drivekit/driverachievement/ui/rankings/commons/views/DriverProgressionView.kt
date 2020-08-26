package com.drivequant.drivekit.driverachievement.ui.rankings.commons.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.DriverProgression
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingViewModel
import kotlinx.android.synthetic.main.dk_driver_progression_view.view.*


class DriverProgressionView  : LinearLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_driver_progression_view, null).setDKStyle()
        addView(view, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))

        setStyle()
    }

    fun setDriverProgression(rankingViewModel: RankingViewModel) {
        val progressionIconId =
            when (rankingViewModel.rankingHeaderData.getProgression(rankingViewModel.previousRank)) {
                DriverProgression.GOING_DOWN -> "dk_achievements_arrow_down"
                DriverProgression.GOING_UP -> "dk_achievements_arrow_up"
                else -> null
            }
        text_view_global_rank.text = rankingViewModel.rankingHeaderData.getDriverGlobalRank(context)
        progressionIconId?.let {
            image_view_driver_progression.setImageDrawable(
                DKResource.convertToDrawable(
                    context,
                    it
                )
            )
        }?:run {
            image_view_driver_progression.visibility = View.GONE
        }
    }

    private fun setStyle() {
        text_view_global_rank.headLine2()
    }
}