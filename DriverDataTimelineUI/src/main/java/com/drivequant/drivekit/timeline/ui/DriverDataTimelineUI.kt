package com.drivequant.drivekit.timeline.ui

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataTimelineUIEntryPoint
import com.drivequant.drivekit.timeline.ui.timeline.TimelineActivity
import com.drivequant.drivekit.timeline.ui.timelinedetail.TimelineDetailActivity

object DriverDataTimelineUI : DriverDataTimelineUIEntryPoint {

    internal const val TAG = "DriveKit Driver Data Timeline UI"

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

    override fun createTimelineFragment(): Fragment {
         TODO()
    }

    override fun createTimelineDetailFragment(): Fragment {
         TODO()
    }
}