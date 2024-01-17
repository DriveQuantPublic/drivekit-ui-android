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
import com.drivequant.drivekit.driverachievement.ui.R

class RankingViewModel : ViewModel() {
    var syncStatus: RankingSyncStatus = RankingSyncStatus.NO_ERROR
    var rankingDriversData = listOf<RankingDriverData>()
        private set
    var mutableLiveDataRankingData: MutableLiveData<RankingData> = MutableLiveData()
    lateinit var fetchedRanking: Ranking
    val rankingSelectorsData = mutableListOf<RankingSelectorData>()
    val rankingTypesData = mutableListOf<RankingTypeData>()
    lateinit var rankingData: RankingData
    var selectedRankingSelectorData: RankingSelectorData
    var selectedRankingTypeData: RankingTypeData
    val useCache: MutableMap<String, Boolean> = mutableMapOf()
    private lateinit var synchronizationType: SynchronizationType

    init {
        for (rankingType in DriverAchievementUI.rankingTypes) {
            val iconId = when (rankingType) {
                RankingType.DISTRACTION -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_distraction_flat
                RankingType.ECO_DRIVING -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_ecodriving_flat
                RankingType.SAFETY -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_safety_flat
                RankingType.SPEEDING -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_speeding_flat
            }
            rankingTypesData.add(RankingTypeData(iconId, rankingType))
        }
        selectedRankingTypeData = rankingTypesData.first()

        when (val rankingSelectorType = DriverAchievementUI.rankingSelector) {
            is RankingSelectorType.NONE -> {
                selectedRankingSelectorData = RankingSelectorData(0, R.string.dk_achievements_ranking_week, RankingPeriod.WEEKLY)
            }
            is RankingSelectorType.PERIOD -> {
                for ((index, rankingPeriod) in rankingSelectorType.rankingPeriods.distinct()
                    .withIndex()) {
                    val titleId = when (rankingPeriod) {
                        RankingPeriod.WEEKLY -> R.string.dk_achievements_ranking_week
                        RankingPeriod.LEGACY -> R.string.dk_achievements_ranking_legacy
                        RankingPeriod.MONTHLY -> R.string.dk_achievements_ranking_month
                        RankingPeriod.ALL_TIME -> R.string.dk_achievements_ranking_permanent
                    }
                    rankingSelectorsData.add(RankingSelectorData(index, titleId, rankingPeriod))
                }
                selectedRankingSelectorData = rankingSelectorsData.first()
            }
        }
    }

    fun fetchRankingList(groupName: String? = null) {
        val useCacheKey = "${selectedRankingTypeData.rankingType}-${selectedRankingSelectorData.rankingPeriod}"
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
            groupName = groupName,
            listener = object : RankingQueryListener {
                override fun onResponse(
                    rankingSyncStatus: RankingSyncStatus,
                    ranking: Ranking
                ) {
                    fetchedRanking = ranking
                    syncStatus = rankingSyncStatus
                    rankingDriversData = buildRankingDriverData(ranking.driversRanked)
                    rankingData = RankingData(this@RankingViewModel)
                    mutableLiveDataRankingData.postValue(rankingData)
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

    private fun buildRankingDriverData(driversRanked: List<DriverRanked>): List<RankingDriverData> {
        val rankingDriversData = mutableListOf<RankingDriverData>()
        var alreadyInserted = false
        for ((index, driverRanked) in driversRanked.withIndex()) {
            rankingDriversData.add(
                RankingDriverData(
                    driverRanked.rank,
                    driverRanked.nickname,
                    driverRanked.distance,
                    driverRanked.score,
                    driverRanked.userId,
                    false
                )
            )
            if (index + 1 != driverRanked.rank && !alreadyInserted) {
                rankingDriversData.add(index, RankingDriverData(
                    driverRanked.rank,
                    driverRanked.nickname,
                    driverRanked.distance,
                    driverRanked.score,
                    driverRanked.userId,
                    true
                ))
                alreadyInserted = true
            }
        }
        return rankingDriversData
    }
}
