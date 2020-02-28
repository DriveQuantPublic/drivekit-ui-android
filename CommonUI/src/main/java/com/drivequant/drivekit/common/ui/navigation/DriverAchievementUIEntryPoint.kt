package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import android.support.v4.app.Fragment

interface DriverAchievementUIEntryPoint {
    fun startStreakActivity(context: Context, className: Class<Any>)
    fun createStreakFragment(): Fragment
}