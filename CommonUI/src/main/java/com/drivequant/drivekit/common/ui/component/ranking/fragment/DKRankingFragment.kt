package com.drivequant.drivekit.common.ui.component.ranking.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRanking
import com.drivequant.drivekit.common.ui.component.ranking.adapter.RankingListAdapter
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DKRankingViewModel
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.FontUtils
import kotlinx.android.synthetic.main.dk_fragment_ranking_component.*

class DKRankingFragment(private val rankingComponent: DKDriverRanking) : Fragment() {

    private lateinit var rankingAdapter: RankingListAdapter
    private lateinit var viewModel: DKRankingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dk_fragment_ranking_component, container, false)
        view.setBackgroundColor(Color.WHITE)
        FontUtils.overrideFonts(context, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(DKRankingViewModel::class.java)
        }
        viewModel.setDKDriverRanking(rankingComponent)
        dk_recycler_view_ranking.layoutManager =
            LinearLayoutManager(requireContext())
        if (this::rankingAdapter.isInitialized) {
            rankingAdapter.notifyDataSetChanged()
        } else {
            rankingAdapter =
                RankingListAdapter(
                    requireContext(),
                    viewModel
                )
            dk_recycler_view_ranking.adapter = rankingAdapter
        }
        dk_ranking_header_view.setHeaderData(viewModel)
        dk_text_view_score_header.text = rankingComponent.getScoreTitle(requireContext())
        setStyle()
    }

    private fun setStyle() {
        dk_text_view_position_header.normalText(DriveKitUI.colors.complementaryFontColor())
        dk_text_view_pseudo_header.normalText(DriveKitUI.colors.complementaryFontColor())
        dk_text_view_score_header.normalText(DriveKitUI.colors.complementaryFontColor())
        dk_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}