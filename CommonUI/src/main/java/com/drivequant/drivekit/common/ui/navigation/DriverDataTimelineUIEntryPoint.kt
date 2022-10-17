package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import androidx.fragment.app.Fragment

interface DriverDataTimelineUIEntryPoint {
    fun startTimelineActivity(context: Context)
    fun startTimelineDetailActivity(context: Context)
    fun createTimelineFragment(): Fragment
    fun createTimelineDetailFragment(): Fragment
}