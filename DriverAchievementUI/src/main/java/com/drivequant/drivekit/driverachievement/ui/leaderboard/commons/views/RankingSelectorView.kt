package com.drivequant.drivekit.driverachievement.ui.leaderboard.commons.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKUtils
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.RankingSelectorListener
import com.drivequant.drivekit.driverachievement.ui.leaderboard.setMarginLeft
import com.drivequant.drivekit.driverachievement.ui.leaderboard.setMarginRight
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingSelectorData

class RankingSelectorView(context: Context) : LinearLayout(context) {
    private var selector: Button
    lateinit var rankingSelectorListener: RankingSelectorListener

    init {
        val view = View.inflate(context, R.layout.dk_ranking_selector_view, null).setDKStyle()
        selector = view.findViewById(R.id.button_selector)
        addView(view)
        setStyle()
    }

    fun configureRankingSelector(rankingSelectorData: RankingSelectorData) {
        selector.text = DKResource.convertToString(context, rankingSelectorData.titleId)
        selector.setOnClickListener {
            rankingSelectorListener.onClickSelector(rankingSelectorData, this)
        }
    }

    private fun setStyle() {
        selector.setMarginLeft(context.resources.getDimension(R.dimen.dk_margin_half).toInt())
        selector.setMarginRight(context.resources.getDimension(R.dimen.dk_margin_half).toInt())
        selector.normalText()
        DKUtils.setBackgroundDrawableColor(selector.background as GradientDrawable, DriveKitUI.colors.neutralColor())
    }

    fun setRankingSelectorSelected(selected: Boolean) {
        if (selected) {
            DKUtils.setBackgroundDrawableColor(
                selector.background as GradientDrawable,
                DriveKitUI.colors.secondaryColor()
            )
            selector.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        } else {
            DKUtils.setBackgroundDrawableColor(
                selector.background as GradientDrawable,
                DriveKitUI.colors.neutralColor()
            )
            selector.normalText()
        }
    }
}

