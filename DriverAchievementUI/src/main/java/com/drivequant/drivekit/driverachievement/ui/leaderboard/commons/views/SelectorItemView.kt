package com.drivequant.drivekit.driverachievement.ui.leaderboard.commons.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.RankingSelectorListener

class SelectorItemView(context: Context?) : LinearLayout(context) {


    private var textViewSelector: TextView
    lateinit var rankingSelectorListener: RankingSelectorListener
    private var isChecked = false

    init {
        val view = View.inflate(context, R.layout.dk_selector_item, null).setDKStyle()
        textViewSelector = view.findViewById(R.id.text_view_selector)

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setStyle()
    }

    fun setSelectorText(text: String) {
        textViewSelector.text = text
    }

    fun onClickSelector(rankingPeriod: RankingPeriod) {
        textViewSelector.setOnClickListener {
            rankingSelectorListener.onClickSelector(rankingPeriod)
        }
    }

    private fun setStyle() {
        textViewSelector.normalText()
        textViewSelector.setBackgroundResource(R.drawable.dk_ranking_selector_rectangle)

    }
}