package com.drivequant.drivekit.timeline.ui

import android.content.Context
import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataTimelineUIEntryPoint
import com.drivequant.drivekit.timeline.ui.timeline.TimelineActivity
import com.drivequant.drivekit.timeline.ui.timeline.TimelineFragment

object DriveKitDriverDataTimelineUI : DriverDataTimelineUIEntryPoint {

    var scores: List<DKScoreType> = DKScoreType.values().toList()
        get() = field.filter { it.hasAccess() }
        set(value) {
            field = value.ifEmpty {
                listOf(DKScoreType.SAFETY)
            }
        }

    fun initialize() {
        DriveKitNavigationController.driverDataTimelineUIEntryPoint = this
    }

    override fun startTimelineActivity(context: Context) {
        TimelineActivity.launchActivity(context)
    }
}