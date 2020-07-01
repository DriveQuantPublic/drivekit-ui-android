package com.drivequant.drivekit.driverachievement.ui.leaderboard

import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod

sealed class RankingSelectorType {
    object NONE : RankingSelectorType()
    data class PERIOD(val rankingPeriods: List<RankingPeriod>) : RankingSelectorType()
}

