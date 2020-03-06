package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import android.support.v4.app.Fragment

interface DriverAchievementUIEntryPoint {
    fun startStreakListActivity(context: Context)
    fun createStreakListFragment(): Fragment
}