package com.drivequant.drivekit.timeline.ui

import android.content.Context
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataTimelineUIEntryPoint
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.timeline.ui.timeline.TimelineActivity

object DriveKitDriverDataTimelineUI : DriverDataTimelineUIEntryPoint {
    internal const val TAG = "DriveKit Driver Data Timeline UI"

    init {
        DriveKit.checkInitialization()
        DriveKitLog.i(TAG, "Initialization")
        DriveKitNavigationController.driverDataTimelineUIEntryPoint = this
    }

    @JvmStatic
    fun initialize() {
        // Nothing to do currently.
    }

    override fun startTimelineActivity(context: Context) {
        TimelineActivity.launchActivity(context)
    }
}
