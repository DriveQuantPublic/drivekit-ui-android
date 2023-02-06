package com.drivequant.drivekit.common.ui.component.ranking.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRanking
import com.drivequant.drivekit.common.ui.component.ranking.adapter.RankingListAdapter
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DKRankingViewModel
import com.drivequant.drivekit.common.ui.extension.normalText
import kotlinx.android.synthetic.main.dk_fragment_ranking_component.view.*

class DKRankingView(context: Context) : LinearLayout(context) {

    private lateinit var rankingAdapter: RankingListAdapter
    private lateinit var viewModel: DKRankingViewModel

    init {
        val view = View.inflate(context, R.layout.dk_fragment_ranking_component, null)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        if (!this::viewModel.isInitialized) {
            viewModel = DKRankingViewModel()
        }
        setStyle()
    }
    private fun setStyle() {
        dk_text_view_position_header.normalText(DriveKitUI.colors.complementaryFontColor())
        dk_text_view_pseudo_header.normalText(DriveKitUI.colors.complementaryFontColor())
        dk_text_view_score_header.normalText(DriveKitUI.colors.complementaryFontColor())
        dk_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }

    fun configure(rankingComponent: DKDriverRanking) {
        viewModel.setDKDriverRanking(rankingComponent)
        dk_recycler_view_ranking.layoutManager =
            LinearLayoutManager(context)
        if (this::rankingAdapter.isInitialized) {
            rankingAdapter.update()
        } else {
            rankingAdapter =
                RankingListAdapter(
                    context,
                    viewModel
                )
            dk_recycler_view_ranking.adapter = rankingAdapter
        }
        dk_ranking_header_view.setHeaderData(viewModel)
        dk_text_view_score_header.text = rankingComponent.getScoreTitle(context)
        root.setBackgroundColor(rankingComponent.getBackgroundColor())
    }
}
