package com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel

import com.drivequant.drivekit.databaseutils.entity.DriverRanked
import com.drivequant.drivekit.databaseutils.entity.Ranking
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.ui.R

class RankingListData(private val ranking: Ranking) {
    fun getStatus(): RankingStatus {
        return RankingStatus.GOING_DOWN
    }

    fun getNbDriverRanked(): Int = ranking.nbDriverRanked

    fun getRankingType(): String = ranking.rankingType.name

    fun getIcon(): Int = R.drawable.badge_acceleration_default_icon_1

    fun getDriverRanked(): List<DriverRanked> = ranking.driversRanked
}