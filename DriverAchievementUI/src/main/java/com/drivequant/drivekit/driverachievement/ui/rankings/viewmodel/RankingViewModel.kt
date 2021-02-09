package com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.DriverRanked
import com.drivequant.drivekit.databaseutils.entity.Ranking
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverachievement.RankingQueryListener
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI

class RankingViewModel : ViewModel() {
    var previousRank: Int? = null
    var syncStatus: RankingSyncStatus = RankingSyncStatus.NO_ERROR
    var rankingDriversData = mutableListOf<RankingDriverData?>()
    var mutableLiveDataRankingHeaderData: MutableLiveData<RankingHeaderData> = MutableLiveData()
    val rankingSelectorsData = mutableListOf<RankingSelectorData>()
    val rankingTypesData = mutableListOf<RankingTypeData>()
    lateinit var rankingHeaderData: RankingHeaderData
    var selectedRankingSelectorData: RankingSelectorData
    var selectedRankingTypeData: RankingTypeData
    val useCache: MutableMap<String, Boolean> = mutableMapOf()
    lateinit var synchronizationType: SynchronizationType

    init {
        for (rankingType in DriverAchievementUI.rankingTypes) {
            val iconId = when (rankingType) {
                RankingType.DISTRACTION -> "dk_achievements_phone_distraction"
                RankingType.ECO_DRIVING -> "dk_achievements_ecodriving"
                RankingType.SAFETY -> "dk_achievements_safety"
                RankingType.SPEEDING -> "dk_achievements_speeding"
            }
            rankingTypesData.add(RankingTypeData(iconId, rankingType))
        }
        selectedRankingTypeData = rankingTypesData.first()

        when (val rankingSelectorType = DriverAchievementUI.rankingSelector) {
            is RankingSelectorType.NONE -> {
                selectedRankingSelectorData =
                    RankingSelectorData(0, "dk_achievements_ranking_week", RankingPeriod.WEEKLY)
            }
            is RankingSelectorType.PERIOD -> {
                for ((index, rankingPeriod) in rankingSelectorType.rankingPeriods.distinct()
                    .withIndex()) {
                    val titleId = when (rankingPeriod) {
                        RankingPeriod.WEEKLY -> "dk_achievements_ranking_week"
                        RankingPeriod.LEGACY -> "dk_achievements_ranking_legacy"
                        RankingPeriod.MONTHLY -> "dk_achievements_ranking_month"
                        RankingPeriod.ALL_TIME -> "dk_achievements_ranking_permanent"
                    }
                    rankingSelectorsData.add(RankingSelectorData(index, titleId, rankingPeriod))
                }
                selectedRankingSelectorData = rankingSelectorsData.first()
            }
        }
    }

    fun fetchRankingList() {
        val useCacheKey =
            "${selectedRankingTypeData.rankingType}-${selectedRankingSelectorData.rankingPeriod}"
        synchronizationType = if (useCache[useCacheKey] == true) {
            SynchronizationType.CACHE
        } else {
            SynchronizationType.DEFAULT
        }

        DriveKitDriverAchievement.getRanking(
            rankingType = selectedRankingTypeData.rankingType,
            rankingPeriod = selectedRankingSelectorData.rankingPeriod,
            rankingDepth = DriverAchievementUI.rankingDepth,
            synchronizationType = synchronizationType,
            listener = object : RankingQueryListener {
                override fun onResponse(
                    rankingSyncStatus: RankingSyncStatus,
                    ranking: Ranking
                ) {
                    previousRank = ranking.userPreviousPosition
                    syncStatus = rankingSyncStatus
                    rankingDriversData = buildRankingDriverData(ranking.driversRanked)
                    rankingHeaderData = RankingHeaderData(ranking)
                    mutableLiveDataRankingHeaderData.postValue(rankingHeaderData)
                    val isSynchronized = when (syncStatus) {
                        RankingSyncStatus.USER_NOT_RANKED,
                        RankingSyncStatus.CACHE_DATA_ONLY,
                        RankingSyncStatus.NO_ERROR -> true
                        RankingSyncStatus.FAILED_TO_SYNC_RANKING_CACHE_ONLY,
                        RankingSyncStatus.SYNC_ALREADY_IN_PROGRESS -> false
                    }
                    useCache[useCacheKey] = isSynchronized
                }
            })
    }

    private fun buildRankingDriverData(driversRanked: List<DriverRanked>): MutableList<RankingDriverData?> {
        rankingDriversData.clear()
        var alreadyInserted = false
        for ((index, driverRanked) in driversRanked.withIndex()) {
            rankingDriversData.add(
                RankingDriverData(
                    driverRanked.rank,
                    driverRanked.nickname,
                    driverRanked.distance,
                    driverRanked.score,
                    driverRanked.userId
                )
            )
            if (index + 1 != driverRanked.rank && !alreadyInserted) {
                rankingDriversData.add(index, null)
                alreadyInserted = true
            }
        }
        return rankingDriversData
    }
}

