package com.drivequant.drivekit.driverachievement.ui.rankings.commons.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorData
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorListener

class RankingSelectorView(context: Context) : LinearLayout(context) {
    lateinit var rankingSelectorListener: RankingSelectorListener
    private val selectorTextView: TextView

    init {
        val view = View.inflate(context, R.layout.dk_ranking_selector_view, null).setDKStyle()
        addView(view)
        this.selectorTextView = view.findViewById(R.id.text_view_selector)
        setStyle()
    }

    fun configureRankingSelector(rankingSelectorData: RankingSelectorData) {
        selectorTextView.setText(rankingSelectorData.titleId)
        selectorTextView.setOnClickListener {
            rankingSelectorListener.onClickSelector(rankingSelectorData, this)
        }
    }

    private fun setStyle() {
        val params = selectorTextView.layoutParams as MarginLayoutParams
        params.setMargins(
            context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_margin_quarter).toInt(),
            params.topMargin,
            context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_margin_quarter).toInt(),
            params.bottomMargin
        )
        layoutParams = params
        selectorTextView.normalText()
        (selectorTextView.background as GradientDrawable).setColor(DriveKitUI.colors.neutralColor())
    }

    fun setRankingSelectorSelected(selected: Boolean) {
        if (selected) {
            (selectorTextView.background as GradientDrawable).setColor(DriveKitUI.colors.secondaryColor())
            selectorTextView.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        } else {
            (selectorTextView.background as GradientDrawable).setColor(DriveKitUI.colors.neutralColor())
            selectorTextView.normalText()
        }
    }
}
