package com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.Streak
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverachievement.StreakSyncStatus
import com.drivequant.drivekit.driverachievement.StreaksQueryListener
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI

class StreaksListViewModel : ViewModel() {

    var filteredStreaksData: MutableList<StreaksData> = mutableListOf()
    val streaksData: MutableLiveData<List<StreaksData>> = MutableLiveData()
    var syncStatus: StreakSyncStatus = StreakSyncStatus.NO_ERROR

    fun fetchStreaks() {
        if (DriveKit.isConfigured()) {
            DriveKitDriverAchievement.getStreaks(object : StreaksQueryListener {
                override fun onResponse(
                    streakSyncStatus: StreakSyncStatus,
                    streaks: List<Streak>) {
                    syncStatus = streakSyncStatus
                    filteredStreaksData = getFilteredStreaks(streaks)
                    streaksData.postValue(filteredStreaksData)
                }
            })
        } else {
            streaksData.postValue(listOf())
        }
    }

    fun getFilteredStreaks(fetchedStreaks: List<Streak>): MutableList<StreaksData> {
        if (fetchedStreaks.isNotEmpty()) {
            filteredStreaksData.clear()
            for (streakTheme in DriverAchievementUI.streakThemes) {
                fetchedStreaks.find { it.theme == streakTheme }?.let { streak ->
                    filteredStreaksData.add(
                        StreaksData(
                            streak.theme,
                            streak.best,
                            streak.current
                        )
                    )
                }
            }
        }
        return filteredStreaksData
    }
}