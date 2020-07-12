package com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.drivequant.drivekit.databaseutils.entity.DriverRanked
import com.drivequant.drivekit.databaseutils.entity.Ranking
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverachievement.RankingQueryListener
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.leaderboard.RankingSelectorType

class RankingViewModel : ViewModel() {
    var previousRank: Int = 0
    var rankingListData = mutableListOf<RankingDriverData>()
    lateinit var rankingHeaderData: RankingHeaderData
    var syncStatus: RankingSyncStatus = RankingSyncStatus.NO_ERROR
    var mutableLiveDataRankingHeaderData: MutableLiveData<RankingHeaderData> = MutableLiveData()
    val rankingSelectorsData = mutableListOf<RankingSelectorData>()
    val rankingTypesData = mutableListOf<RankingTypeData>()
    var selectedRankingSelectorData: RankingSelectorData
    var selectedRankingTypeData: RankingTypeData

    init {
        for (rankingType in DriverAchievementUI.rankingTypes) {
            val iconId = when(rankingType) {
                RankingType.DISTRACTION -> "dk_achievements_phone_distraction"
                RankingType.ECO_DRIVING -> "dk_achievements_ecodriving"
                RankingType.SAFETY -> "dk_achievements_safety"
            }
            rankingTypesData.add(RankingTypeData(iconId, rankingType))
        }
        selectedRankingTypeData = rankingTypesData.first()

        when (val rankingSelectorType = DriverAchievementUI.rankingSelector) {
            is RankingSelectorType.NONE -> {}
            is RankingSelectorType.PERIOD -> {
                for ((index, rankingPeriod) in rankingSelectorType.rankingPeriods.withIndex()) {
                    val titleId = when (rankingPeriod) {
                        RankingPeriod.WEEKLY -> "dk_achievements_ranking_week"
                        RankingPeriod.LEGACY -> "dk_achievements_ranking_two_weeks"
                        RankingPeriod.MONTHLY -> "dk_achievements_ranking_month"
                        RankingPeriod.ALL_TIME -> "dk_achievement_ranking_permanent"
                    }
                    rankingSelectorsData.add(RankingSelectorData(index, titleId,rankingPeriod))
                }
            }
        }
        selectedRankingSelectorData = rankingSelectorsData.first()
    }

    fun fetchRankingList() {
        //TODO check if DriveKit is configured
        DriveKitDriverAchievement.getRanking(
            rankingType = selectedRankingTypeData.rankingType,
            rankingPeriod = selectedRankingSelectorData.rankingPeriod,
            rankingDepth = DriverAchievementUI.rankingDepth,
            listener = object : RankingQueryListener {
                override fun onResponse(
                    rankingSyncStatus: RankingSyncStatus,
                    ranking: Ranking,
                    driverPreviousRank: Int) {
                    syncStatus = rankingSyncStatus
                    previousRank = driverPreviousRank
                    rankingListData = buildRankingListData(ranking.driversRanked)
                    rankingHeaderData = RankingHeaderData(ranking)
                    mutableLiveDataRankingHeaderData.postValue(rankingHeaderData)
                }
            })
    }

    fun buildRankingListData(driversRanked: List<DriverRanked>): MutableList<RankingDriverData> {
        rankingListData.clear()
        for (driverRanked in driversRanked) {
            rankingListData.add(
                RankingDriverData(
                    driverRanked.rank,
                    driverRanked.nickname,
                    driverRanked.distance,
                    driverRanked.score,
                    driverRanked.userId))
        }
        return rankingListData
    }
}
