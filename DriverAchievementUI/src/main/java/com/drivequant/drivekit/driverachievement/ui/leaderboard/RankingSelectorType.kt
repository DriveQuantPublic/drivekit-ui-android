package com.drivequant.drivekit.driverachievement.ui.leaderboard

import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.leaderboard.commons.views.RankingSelectorView
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingSelectorData

sealed class RankingSelectorType {
    object NONE : RankingSelectorType()
    data class PERIOD(val rankingPeriods: List<RankingPeriod>) : RankingSelectorType()
}

interface RankingSelectorListener {
    fun onClickSelector(rankingSelectorData:RankingSelectorData, rankingSelectorView: RankingSelectorView)
}

