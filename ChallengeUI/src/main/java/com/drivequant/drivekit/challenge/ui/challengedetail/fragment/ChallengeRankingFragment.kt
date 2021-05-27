package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRanking
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRankingItem
import com.drivequant.drivekit.common.ui.component.ranking.RankingHeaderDisplayType
import com.drivequant.drivekit.common.ui.component.ranking.fragment.DKRankingFragment
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DriverProgression


class ChallengeRankingFragment : Fragment() {

    private lateinit var viewModel: ChallengeDetailViewModel
    private lateinit var fragment: DKRankingFragment

    companion object {
        fun newInstance(viewModel: ChallengeDetailViewModel): ChallengeRankingFragment {
            val fragment = ChallengeRankingFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putString("challengeIdTag", viewModel.getChallengeId())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_challenge_ranking, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment = DKRankingFragment()
        fragmentManager?.beginTransaction()?.replace(R.id.container, fragment)?.commit()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.getString("challengeIdTag")?.let {
            viewModel = ViewModelProviders.of(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
            ).get(ChallengeDetailViewModel::class.java)
        }

        viewModel.getChallengeDetail()

        viewModel.challengeDetail.observe(requireActivity(), Observer {
            fragment.configureDKDriverRanking(object : DKDriverRanking {
                override fun getHeaderDisplayType(): RankingHeaderDisplayType =
                    RankingHeaderDisplayType.COMPACT

                override fun getTitle(): String = ""

                override fun getIcon(context: Context): Drawable? = null

                override fun getProgression(): DriverProgression? = null

                override fun getDriverGlobalRank(context: Context): Spannable = viewModel.getDriverGlobalRank(requireContext())

                override fun getScoreTitle(context: Context): String = viewModel.getScoreTitle()

                override fun getDriverRankingList(): List<DKDriverRankingItem> = viewModel.getRankingList()
            })
        })
    }
}