package com.drivequant.drivekit.timeline.ui

import android.content.Context
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataTimelineUIEntryPoint
import com.drivequant.drivekit.timeline.ui.timeline.DKTimelineScoreType
import com.drivequant.drivekit.timeline.ui.timeline.TimelineActivity
import com.drivequant.drivekit.timeline.ui.timeline.TimelineFragment

object DriveKitDriverDataTimelineUI : DriverDataTimelineUIEntryPoint {

    var scoresType: List<DKTimelineScoreType> = DKTimelineScoreType.values().toList()
        get() = field.filter { it.hasAccess() }
        set(value) {
            field = value.ifEmpty {
                val timelineScores = mutableListOf<DKTimelineScoreType>()
                timelineScores.add(DKTimelineScoreType.SAFETY)
                timelineScores
            }
        }

    fun initialize() {
        DriveKitNavigationController.driverDataTimelineUIEntryPoint = this
    }

    override fun startTimelineActivity(context: Context) {
        TimelineActivity.launchActivity(context)
    }

    override fun createTimelineFragment() = TimelineFragment.newInstance()

}