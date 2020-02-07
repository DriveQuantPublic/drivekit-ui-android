package com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.Streak
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverachievement.streak.AchievementSyncStatus
import com.drivequant.drivekit.driverachievement.streak.StreaksQueryListener

class StreaksViewModel : ViewModel() {

    var sortedStreaks: MutableList<StreaksData> = mutableListOf()
    val streaksData: MutableLiveData<List<StreaksData>> = MutableLiveData()
    var syncStatus: AchievementSyncStatus = AchievementSyncStatus.NO_ERROR

    fun fetchStreaks(streaksToDisplay: List<StreakTheme>) {
        if (DriveKit.isConfigured()) {
            DriveKitDriverAchievement.getStreaks(object : StreaksQueryListener {
                override fun onResponse(
                    achievementSyncStatus: AchievementSyncStatus,
                    streaks: List<Streak>
                ) {
                    syncStatus = achievementSyncStatus
                    sortedStreaks = getFilteredStreaks(streaksToDisplay, streaks)
                    streaksData.postValue(sortedStreaks)
                }
            })
        } else {
            streaksData.postValue(listOf())
        }
    }

    fun getFilteredStreaks(
        streaksToDisplay: List<StreakTheme>,
        fetchedStreaks: List<Streak>
    ): MutableList<StreaksData> {
        if (fetchedStreaks.isNotEmpty()) {
            val filteredStreaks =
                fetchedStreaks.filter { streak -> streak.theme in streaksToDisplay }
            sortedStreaks.clear()
            for (streak in filteredStreaks) {
                sortedStreaks.add(
                    StreaksData(
                        streak.theme,
                        streak.best,
                        streak.current
                    )
                )
            }
        }
        return sortedStreaks
    }
}