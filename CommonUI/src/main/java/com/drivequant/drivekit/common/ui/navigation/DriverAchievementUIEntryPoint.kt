package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import androidx.fragment.app.Fragment

interface DriverAchievementUIEntryPoint {
    fun startStreakListActivity(context: Context)
    fun createStreakListFragment(): Fragment
}