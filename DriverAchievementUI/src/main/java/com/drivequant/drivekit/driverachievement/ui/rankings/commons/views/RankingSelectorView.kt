package com.drivequant.drivekit.driverachievement.ui.rankings.commons.views

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
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorListener
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorData
import kotlinx.android.synthetic.main.dk_ranking_selector_view.view.*

class RankingSelectorView(context: Context) : LinearLayout(context) {
    lateinit var rankingSelectorListener: RankingSelectorListener

    init {
        val view = View.inflate(context, R.layout.dk_ranking_selector_view, null).setDKStyle()
        addView(view)
        setStyle()
    }

    fun configureRankingSelector(rankingSelectorData: RankingSelectorData) {
        text_view_selector.text = DKResource.convertToString(context, rankingSelectorData.titleId)
        text_view_selector.setOnClickListener {
            rankingSelectorListener.onClickSelector(rankingSelectorData, this)
        }
    }

    private fun setStyle() {
        val params = text_view_selector.layoutParams as MarginLayoutParams
        params.setMargins(
            context.resources.getDimension(R.dimen.dk_margin_half).toInt(),
            params.topMargin,
            context.resources.getDimension(R.dimen.dk_margin_half).toInt(),
            params.bottomMargin
        )
        layoutParams = params
        text_view_selector.normalText()
        DKUtils.setBackgroundDrawableColor(text_view_selector.background as GradientDrawable, DriveKitUI.colors.neutralColor())
    }

    fun setRankingSelectorSelected(selected: Boolean) {
        if (selected) {
            DKUtils.setBackgroundDrawableColor(
                text_view_selector.background as GradientDrawable,
                DriveKitUI.colors.secondaryColor()
            )
            text_view_selector.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        } else {
            DKUtils.setBackgroundDrawableColor(
                text_view_selector.background as GradientDrawable,
                DriveKitUI.colors.neutralColor()
            )
            text_view_selector.normalText()
        }
    }
}

