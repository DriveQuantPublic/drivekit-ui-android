package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.fragment.ChallengeRankingFragment
import com.drivequant.drivekit.challenge.ui.challengedetail.fragment.ChallengeResultsFragment
import com.drivequant.drivekit.challenge.ui.challengedetail.fragment.ChallengeRulesFragment
import com.drivequant.drivekit.challenge.ui.challengedetail.fragment.ChallengeTripListFragment

enum class ChallengeDetailItem {
    RESULTS, RANKING, TRIPS, RULES;

    fun getImageResource(): Int = when (this) {
        RESULTS -> R.drawable.dk_challenge_result
        RANKING -> R.drawable.dk_challenge_leaderboard
        TRIPS -> R.drawable.dk_challenge_trip
        RULES -> R.drawable.dk_challenge_rules
    }

    fun getFragment(viewModel: ChallengeDetailViewModel) = when (this) {
        RESULTS -> ChallengeResultsFragment.newInstance(viewModel)
        RANKING -> ChallengeRankingFragment.newInstance(viewModel)
        TRIPS ->  ChallengeTripListFragment.newInstance(viewModel)
        RULES -> ChallengeRulesFragment.newInstance(viewModel.challenge.challengeId)
    }
}