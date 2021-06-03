package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDriverRanking
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.ranking.fragment.DKRankingFragment
import com.drivequant.drivekit.common.ui.utils.DKResource


class ChallengeRankingFragment : Fragment() {

    private lateinit var viewModel: ChallengeDetailViewModel

    companion object {
        fun newInstance(viewModel: ChallengeDetailViewModel):ChallengeRankingFragment {
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_challenge_detail_ranking"
            ), javaClass.simpleName
        )

        savedInstanceState?.getString("challengeIdTag")?.let {
            if (!this::viewModel.isInitialized) {
                viewModel = ViewModelProviders.of(
                    this,
                    ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
                ).get(ChallengeDetailViewModel::class.java)
            }
        }

        fragmentManager?.beginTransaction()
            ?.replace(R.id.container, DKRankingFragment(ChallengeDriverRanking(viewModel)))
            ?.commit()
    }
}