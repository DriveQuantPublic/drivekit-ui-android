package com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.Ranking
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverachievement.RankingQueryListener
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI

class RankingListViewModel : ViewModel() {
    lateinit var rankingListData: RankingListData
    var syncStatus: RankingSyncStatus = RankingSyncStatus.NO_ERROR
    var mutableLiveDataRankingListData: MutableLiveData<RankingListData> = MutableLiveData()

    fun fetchRankingList(rankingType: RankingType, rankingPeriod: RankingPeriod) {
            DriveKitDriverAchievement.getRanking(
                rankingType = rankingType,
                rankingPeriod = rankingPeriod,
                rankingDepth = DriverAchievementUI.rankingDepth,
                listener = object : RankingQueryListener {
                    override fun onResponse(
                        rankingSyncStatus: RankingSyncStatus,
                        ranking: Ranking
                    ) {
                        syncStatus = rankingSyncStatus
                        val rankingData = RankingListData(ranking)
                        rankingListData = rankingData
                        mutableLiveDataRankingListData.postValue(rankingData)
                    }
                })
    }
}