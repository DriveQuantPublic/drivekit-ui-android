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
    private var buttonSelector: Button
    lateinit var rankingSelectorListener: RankingSelectorListener
    var isChecked: Boolean = false

    init {
        val view = View.inflate(context, R.layout.dk_ranking_selector_view, null).setDKStyle()
        buttonSelector = view.findViewById(R.id.button_selector)
        addView(view)
        setStyle()
    }

    fun configureRankingSelectorButton(rankingSelectorData: RankingSelectorData) {
        buttonSelector.text = DKResource.convertToString(context, rankingSelectorData.titleId)
        buttonSelector.setOnClickListener {
            rankingSelectorListener.onClickSelector(rankingSelectorData, this)
        }
    }

    private fun setStyle() {
        buttonSelector.setMarginLeft(context.resources.getDimension(R.dimen.dk_margin_half).toInt())
        buttonSelector.setMarginRight(context.resources.getDimension(R.dimen.dk_margin_half).toInt())
        buttonSelector.normalText()
        DKUtils.setBackgroundDrawableColor(buttonSelector.background as GradientDrawable, DriveKitUI.colors.neutralColor())
    }

    fun setButtonSelected(selected: Boolean) {
        if (selected) {
            isChecked = true
            DKUtils.setBackgroundDrawableColor(
                buttonSelector.background as GradientDrawable,
                DriveKitUI.colors.secondaryColor()
            )
            buttonSelector.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        } else {
            DKUtils.setBackgroundDrawableColor(
                buttonSelector.background as GradientDrawable,
                DriveKitUI.colors.neutralColor()
            )
            buttonSelector.normalText()
        }
    }
}

