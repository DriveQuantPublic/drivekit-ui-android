package com.drivequant.drivekit.driverachievement.ui.leaderboard.commons.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKUtils
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.extension.setMarginLeft
import com.drivequant.drivekit.driverachievement.ui.leaderboard.extension.setMarginRight
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingSelectorListener
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingSelectorData
import kotlinx.android.synthetic.main.dk_ranking_selector_view.view.*

class RankingSelectorView(context: Context) : LinearLayout(context) {
    lateinit var rankingSelectorListener: RankingSelectorListener

    init {
        val view = View.inflate(context, R.layout.dk_ranking_selector_view, null).setDKStyle()
        addView(view)
        setStyle()
    }

    fun configureRankingSelector(rankingSelectorData: RankingSelectorData) {
        button_selector.text = DKResource.convertToString(context, rankingSelectorData.titleId)
        button_selector.setOnClickListener {
            rankingSelectorListener.onClickSelector(rankingSelectorData, this)
        }
    }

    private fun setStyle() {
        button_selector.setMarginLeft(context.resources.getDimension(R.dimen.dk_margin_half).toInt())
        button_selector.setMarginRight(context.resources.getDimension(R.dimen.dk_margin_half).toInt())
        button_selector.normalText()
        DKUtils.setBackgroundDrawableColor(button_selector.background as GradientDrawable, DriveKitUI.colors.neutralColor())
    }

    fun setRankingSelectorSelected(selected: Boolean) {
        if (selected) {
            DKUtils.setBackgroundDrawableColor(
                button_selector.background as GradientDrawable,
                DriveKitUI.colors.secondaryColor()
            )
            button_selector.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        } else {
            DKUtils.setBackgroundDrawableColor(
                button_selector.background as GradientDrawable,
                DriveKitUI.colors.neutralColor()
            )
            button_selector.normalText()
        }
    }
}

