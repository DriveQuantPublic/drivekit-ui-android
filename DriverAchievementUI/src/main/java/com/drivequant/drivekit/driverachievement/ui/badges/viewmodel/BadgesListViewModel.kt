package com.drivequant.drivekit.driverachievement.ui.badges.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.Badge
import com.drivequant.drivekit.driverachievement.BadgeSyncStatus
import com.drivequant.drivekit.driverachievement.BadgesQueryListener
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverachievement.badge.model.DKBadgeStatistics
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI


internal class BadgesListViewModel : ViewModel() {
    var filteredBadgesData: List<BadgesData> = listOf()
    val badgesData: MutableLiveData<List<BadgesData>> = MutableLiveData()
    var syncStatus: BadgeSyncStatus = BadgeSyncStatus.NO_ERROR
    var badgesStatistics: DKBadgeStatistics? = null

    fun fetchBadges() {
        if (DriveKit.isConfigured()) {
            DriveKitDriverAchievement.getBadges(object : BadgesQueryListener {
                override fun onResponse(
                    badgeSyncStatus: BadgeSyncStatus,
                    badges: List<Badge>,
                    newAcquiredBadgesCount: Int
                ) {
                    syncStatus = badgeSyncStatus
                    filteredBadgesData = getFilteredBadges(badges)
                    badgesStatistics = DriveKitDriverAchievement.getBadgeStatistics()
                    badgesData.postValue(filteredBadgesData)
                }
            })
        } else {
            badgesData.postValue(listOf())
        }
    }

    private fun getFilteredBadges(fetchedBadges: List<Badge>): List<BadgesData> {
        return if (fetchedBadges.isNotEmpty()) {
            val filteredBadges = fetchedBadges.filter { badge -> badge.category in DriverAchievementUI.badgeCategories }
            filteredBadges.map { BadgesData(it.theme, it.badgeCharacteristics) }
        } else {
            emptyList()
        }
    }
}
