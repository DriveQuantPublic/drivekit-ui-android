package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.common.ChallengeHeaderView
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI

internal class ChallengeRulesFragment : Fragment() {

    private lateinit var challengeId: String
    private lateinit var viewModel: ChallengeParticipationViewModel

    companion object {
        fun newInstance(challengeId: String): ChallengeRulesFragment {
            val fragment = ChallengeRulesFragment()
            fragment.challengeId = challengeId
            return fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::challengeId.isInitialized) {
            outState.putString("challengeIdTag", challengeId)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.dk_fragment_challenge_rules, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container = view as LinearLayout
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_challenge_detail_rules), javaClass.simpleName)

        savedInstanceState?.getString("challengeIdTag")?.let {
            viewModel = ViewModelProvider(
                this,
                ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(it)
            )[ChallengeParticipationViewModel::class.java]
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(
                this,
                ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(challengeId)
            )[ChallengeParticipationViewModel::class.java]
        }

        val challengeHeaderView = ChallengeHeaderView(requireContext())
        challengeHeaderView.configure(viewModel, requireActivity())
        container.addView(challengeHeaderView)
    }
}
