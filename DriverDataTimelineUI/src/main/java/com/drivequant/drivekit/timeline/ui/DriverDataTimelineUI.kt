package com.drivequant.drivekit.timeline.ui

import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.timeline.ui.timeline.TimelineActivity
import com.drivequant.drivekit.timeline.ui.timelinedetail.TimelineDetailActivity

object DriverDataTimelineUI {

    fun startTimelineActivity(context: Context) {
        val intent = Intent(context, TimelineActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun startTimelineDetailActivity(context: Context) {
        val intent = Intent(context, TimelineDetailActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun createTimelineFragment() {
        // TODO()
    }

    fun createTimelineDetailFragment() {
        // TODO()
    }
}