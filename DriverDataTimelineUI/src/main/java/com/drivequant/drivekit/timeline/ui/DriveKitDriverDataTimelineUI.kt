package com.drivequant.drivekit.timeline.ui

import android.content.Context
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataTimelineUIEntryPoint
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.timeline.ui.timeline.TimelineActivity

object DriveKitDriverDataTimelineUI : DriverDataTimelineUIEntryPoint {

    @Deprecated("You should use DriveKitUI.scores now.", ReplaceWith("DriveKitUI.scores"))
    var scores: List<DKScoreType>
        get() = DriveKitUI.scores
        set(value) {
            DriveKitUI.scores = value
        }

    fun initialize() {
        DriveKitNavigationController.driverDataTimelineUIEntryPoint = this
    }

    override fun startTimelineActivity(context: Context) {
        TimelineActivity.launchActivity(context)
    }
}
