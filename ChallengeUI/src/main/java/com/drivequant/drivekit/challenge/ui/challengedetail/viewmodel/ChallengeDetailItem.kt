package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

enum class ChallengeDetailItem {
    RESULTS, RANKING, TRIPS, RULES;

    fun getImageResource(): String = when (this) {
        RESULTS -> "dk_challenge_result"
        RANKING -> "dk_challenge_leaderboard"
        TRIPS -> "dk_challenge_trip"
        RULES -> "dk_challenge_rule"
    }

    fun getFragment() = when (this) {
        RESULTS -> 1
        RANKING -> 2
        TRIPS -> 3
        RULES -> 4
    }
}