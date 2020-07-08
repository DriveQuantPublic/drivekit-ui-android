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

class RankingListViewModel : ViewModel() {
    var previousRank: Int = 0
    var rankingListData = mutableListOf<RankingListData>()
    lateinit var leaderBoardData: LeaderBoardData
    var syncStatus: RankingSyncStatus = RankingSyncStatus.NO_ERROR
    var mutableLiveDataLeaderBoardData: MutableLiveData<LeaderBoardData> = MutableLiveData()
    var currentRankingPeriod: RankingPeriod = RankingPeriod.LEGACY

    fun fetchRankingList(rankingType: RankingType) {
        //TODO check if DriveKit is configured
        DriveKitDriverAchievement.getRanking(
            rankingType = rankingType,
            rankingPeriod = currentRankingPeriod,
            rankingDepth = DriverAchievementUI.rankingDepth,
            listener = object : RankingQueryListener {
                override fun onResponse(
                    rankingSyncStatus: RankingSyncStatus,
                    ranking: Ranking,
                    driverPreviousRank: Int
                ) {
                    syncStatus = rankingSyncStatus
                    previousRank = driverPreviousRank
                    rankingListData = buildRankingListData(ranking.driversRanked)
                    leaderBoardData = LeaderBoardData(ranking)
                    mutableLiveDataLeaderBoardData.postValue(leaderBoardData)
                }
            })
    }

    fun buildRankingListData(driversRanked: List<DriverRanked>): MutableList<RankingListData> {
        rankingListData.clear()
        for (driverRanked in driversRanked) {
            rankingListData.add(
                RankingListData(
                    driverRanked.rank,
                    driverRanked.nickname,
                    driverRanked.distance,
                    driverRanked.score,
                    driverRanked.userId
                )
            )
        }
        return rankingListData
    }
}