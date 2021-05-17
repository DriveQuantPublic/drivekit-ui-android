package com.drivequant.drivekit.challenge.ui.joinchallenge.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_fragment_challenge_join.*


class ChallengeParticipationFragment : Fragment() {

    private lateinit var challengeId: String
    private lateinit var viewModel: ChallengeParticipationViewModel

    companion object {
        fun newInstance(challengeId: String): ChallengeParticipationFragment {
            val fragment = ChallengeParticipationFragment()
            fragment.challengeId = challengeId
            return fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::challengeId.isInitialized) {
            outState.putString("challengeId", challengeId)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.dk_fragment_challenge_join, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_challenge_join"
            ), javaClass.simpleName
        )

        (savedInstanceState?.getString("challengeId"))?.let { it ->
            challengeId = it
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(
                this,
                ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(challengeId)
            ).get(ChallengeParticipationViewModel::class.java)
        }

        when {
            viewModel.shouldDisplayCountDown() -> {

            }
            viewModel.shouldDisplayJoinChallenge() -> {

            }
            viewModel.shouldDisplayProgressBars() -> {

            }
            else -> {

            }
        }

        setStyle()
    }

    private fun stopCountDown() {

    }

    private fun startCountDown() {

    }

    private fun updateCountDown() {

    }

    private fun setStyle() {

        view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        view_separator_1.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}