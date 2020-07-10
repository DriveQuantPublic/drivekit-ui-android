package com.drivequant.drivekit.driverachievement.ui.leaderboard.commons.views

import android.annotation.SuppressLint
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
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.RankingSelectorListener
import com.drivequant.drivekit.driverachievement.ui.leaderboard.setMarginLeft
import com.drivequant.drivekit.driverachievement.ui.leaderboard.setMarginRight
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingSelector

class SelectorItemView(context: Context) : LinearLayout(context) {
    private var buttonSelector: Button
    lateinit var rankingSelectorListener: RankingSelectorListener
    var isChecked: Boolean = false

    init {
        val view = View.inflate(context, R.layout.dk_selector_item, null).setDKStyle()
        buttonSelector = view.findViewById(R.id.button_selector)
        addView(view)
        setStyle()
    }

    @SuppressLint("SetTextI18n")
    fun setSelectorText(rankingSelector: RankingSelector) {
        buttonSelector.text = "${rankingSelector.title} ${DKResource.convertToString(context, "dk_achievements_ranking_days")}"
    }

    fun onRankingSelectorButtonSelected(rankingPeriod: RankingPeriod) {
        buttonSelector.setOnClickListener {
            rankingSelectorListener.onClickSelector(rankingPeriod)
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
        } else {
            DKUtils.setBackgroundDrawableColor(
                buttonSelector.background as GradientDrawable,
                DriveKitUI.colors.neutralColor()
            )
        }
    }
}

