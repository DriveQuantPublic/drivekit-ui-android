package com.drivequant.drivekit.common.ui.component.ranking.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRanking
import com.drivequant.drivekit.common.ui.component.ranking.adapter.RankingListAdapter
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DKRankingViewModel
import com.drivequant.drivekit.common.ui.extension.normalText

class DKRankingView(context: Context) : LinearLayout(context) {

    private val rootView: View
    private val positionHeader: TextView
    private val pseudoHeader: TextView
    private val scoreHeader: TextView
    private val recyclerView: RecyclerView
    private val headerView: RankingHeaderView
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
        this.rootView = view.findViewById(R.id.root)
        this.positionHeader = view.findViewById(R.id.dk_text_view_position_header)
        this.pseudoHeader = view.findViewById(R.id.dk_text_view_pseudo_header)
        this.scoreHeader = view.findViewById(R.id.dk_text_view_score_header)
        this.recyclerView = view.findViewById(R.id.dk_recycler_view_ranking)
        this.headerView = view.findViewById(R.id.dk_ranking_header_view)

        if (!this::viewModel.isInitialized) {
            viewModel = DKRankingViewModel()
        }
        setStyle()
    }

    private fun setStyle() {
        this.positionHeader.normalText()
        this.pseudoHeader.normalText()
        this.scoreHeader.normalText()
    }

    fun configure(rankingComponent: DKDriverRanking) {
        this.viewModel.setDKDriverRanking(rankingComponent)
        this.recyclerView.layoutManager = LinearLayoutManager(context)
        if (this::rankingAdapter.isInitialized) {
            this.rankingAdapter.update()
        } else {
            this.rankingAdapter =
                RankingListAdapter(
                    context,
                    viewModel
                )
            this.recyclerView.adapter = rankingAdapter
        }
        this.headerView.setHeaderData(viewModel)
        this.scoreHeader.text = rankingComponent.getScoreTitle(context)
        this.rootView.setBackgroundColor(rankingComponent.getBackgroundColor())
    }
}
