package com.drivequant.drivekit.timeline.ui.timeline

import android.util.Log
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.driverdata.timeline.TimelineQueryListener
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.timeline.ui.DKTimelineScore
import com.drivequant.drivekit.timeline.ui.DriverDataTimelineUI

internal class TimelineViewModel : ViewModel() {

    var selectedTimelineScoreType: TimelineScoreData
    var timelineScoreTypes = mutableListOf<TimelineScoreData>()
    var selectedTimelinePeriods = mutableListOf<DKTimelinePeriod>()

    init {
        for (score in DriverDataTimelineUI.scores) {
            timelineScoreTypes.add(TimelineScoreData(score.getIconResId(), score))
        }
        selectedTimelineScoreType = if(timelineScoreTypes.isNotEmpty()) {
            timelineScoreTypes.first()
        } else {
            TimelineScoreData(
                DKTimelineScore.SAFETY.getIconResId(),
                DKTimelineScore.SAFETY
            )
        }
        selectedTimelinePeriods.addAll(DKTimelinePeriod.values())
    }

    fun fetchTimeline() {
        DriveKitDriverData.getTimelines(
            periods = selectedTimelinePeriods,
            listener = object: TimelineQueryListener {
                override fun onResponse(
                    timelineSyncStatus: TimelineSyncStatus,
                    timelines: List<Timeline>
                ) {
                    Log.e("TEST",timelines[0].period.name)
                }
            },
            synchronizationType = SynchronizationType.DEFAULT
        )
    }
}

data class TimelineScoreData(
    val iconId: String,
    var scoreType: DKTimelineScore
)