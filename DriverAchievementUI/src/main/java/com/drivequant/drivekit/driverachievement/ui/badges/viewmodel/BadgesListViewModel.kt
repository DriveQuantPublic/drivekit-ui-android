package com.drivequant.drivekit.driverachievement.ui.badges.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.Badge
import com.drivequant.drivekit.driverachievement.BadgeSyncStatus
import com.drivequant.drivekit.driverachievement.BadgesQueryListener
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI

/**
 * Created by Mohamed on 2020-05-05.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgesListViewModel : ViewModel() {
    var filteredBadgesData: MutableList<BadgesData> = mutableListOf()
    val badgesData: MutableLiveData<List<BadgesData>> = MutableLiveData()
    var syncStatus: BadgeSyncStatus = BadgeSyncStatus.NO_ERROR

    fun fetchBadges() {
        if (DriveKit.isConfigured()) {
            DriveKitDriverAchievement.getBadges(object : BadgesQueryListener {
                override fun onResponse(
                    badgeSyncStatus: BadgeSyncStatus,
                    badges: List<Badge>
                ) {
                    syncStatus = badgeSyncStatus
                    filteredBadgesData = getFilteredBadges(badges)
                    badgesData.postValue(filteredBadgesData)
                }
            })
        } else {
            badgesData.postValue(listOf())
        }
    }

    fun getFilteredBadges(fetchedBadges: List<Badge>): MutableList<BadgesData> {
        if (fetchedBadges.isNotEmpty()) {
            val filteredBadges =
                fetchedBadges.filter { badge -> badge.category in DriverAchievementUI.badgeCategories }
            filteredBadgesData.clear()
            for (badge in filteredBadges) {
                filteredBadgesData.add(
                    BadgesData(
                        badge.themeKey,
                        badge.category,
                        badge.levels
                    )
                )
            }
        }
        return filteredBadgesData
    }
}