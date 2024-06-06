package com.drivequant.drivekit.common.ui.component.scoreselector

import android.content.Context
import android.util.AttributeSet
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.getIconResId
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.google.android.material.tabs.TabLayout

class DKScoreSelectorView : TabLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        this.tabMode = MODE_FIXED
    }

    fun configure(viewModel: DKScoreSelectorViewModel) {
        removeAllTabs()

        val scores = viewModel.scores
        scores.forEach {
            val tab = newTab()
            tab.setIcon(it.getIconResId())
            tab.tag = it
            addTab(tab)
        }

        for (i in 0 until this.tabCount) {
            getTabAt(i)?.setCustomView(R.layout.dk_icon_view_tab)
        }

        setSelectedTabIndicatorColor(DKColors.secondaryColor)
        setBackgroundColor(DKColors.backgroundViewColor)

        addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                val score = tab?.tag as? DKScoreType
                if (score != null) {
                    viewModel.onScoreChange(score)
                }
            }

            override fun onTabUnselected(tab: Tab?) {}
            override fun onTabReselected(tab: Tab?) {}
        })
    }

    fun scoreCount(): Int = this.tabCount
}
