package com.drivequant.drivekit.timeline.ui

import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataTimelineUIEntryPoint
import com.drivequant.drivekit.timeline.ui.timeline.DKTimelineScore
import com.drivequant.drivekit.timeline.ui.timeline.TimelineActivity
import com.drivequant.drivekit.timeline.ui.timeline.TimelineFragment
import com.drivequant.drivekit.timeline.ui.timelinedetail.TimelineDetailActivity
import com.drivequant.drivekit.timeline.ui.timelinedetail.TimelineDetailFragment

object DriverDataTimelineUI : DriverDataTimelineUIEntryPoint {

    internal const val TAG = "DriveKit Driver Data Timeline UI"

    var scoresType: List<DKTimelineScore> = DKTimelineScore.values().toList()
        get() = field.filter { it.hasAccess() }
        set(value) {
            field = value.ifEmpty {
                val timelineScores = mutableListOf<DKTimelineScore>()
                timelineScores.add(DKTimelineScore.SAFETY)
                timelineScores
            }
        }

    fun initialize() {
        DriveKitNavigationController.driverDataTimelineUIEntryPoint = this
    }

    override fun startTimelineActivity(context: Context) {
        val intent = Intent(context, TimelineActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun startTimelineDetailActivity(context: Context) {
        val intent = Intent(context, TimelineDetailActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun createTimelineFragment() = TimelineFragment.newInstance()

    override fun createTimelineDetailFragment() = TimelineDetailFragment.newInstance()
}