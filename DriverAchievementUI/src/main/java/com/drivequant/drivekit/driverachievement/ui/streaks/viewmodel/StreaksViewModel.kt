package com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.databaseutils.entity.streak.Streak
import com.drivequant.drivekit.databaseutils.entity.streak.StreakTheme
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverachievement.streak.AchievementSyncStatus
import com.drivequant.drivekit.driverachievement.streak.StreaksQueryListener

class StreaksViewModel : ViewModel() {

    var sortedStreaks : List<StreaksData> = listOf()
    val streaksData: MutableLiveData<List<StreaksData>> = MutableLiveData()
    var syncStatus: AchievementSyncStatus = AchievementSyncStatus.NO_ERROR

    fun fetchStreaks(streaksToDisplay : List<StreakTheme>) {
        if (DriveKit.isConfigured()) {

            DriveKitDriverAchievement.getStreaks(object : StreaksQueryListener {
                override fun onResponse(
                    achievementSyncStatus: AchievementSyncStatus,
                    streaks: List<Streak>
                ) {
                    syncStatus = achievementSyncStatus
                    sortedStreaks = getSortedStreaks(streaksToDisplay, streaks)
                    streaksData.postValue(sortedStreaks)
                }
            })
        } else {
            streaksData.postValue(mutableListOf())
        }
    }

    fun getSortedStreaks(streaksToDisplay : List<StreakTheme>, fetchedStreaks: List<Streak>): List<StreaksData> {
        fetchedStreaks.forEach{
            DriveKitLog.e("THEME_SYNCHRONIZED",it.theme.name)
        }
        return listOf()
    }
}