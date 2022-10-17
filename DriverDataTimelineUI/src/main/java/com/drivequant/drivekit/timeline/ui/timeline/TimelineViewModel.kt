package com.drivequant.drivekit.timeline.ui.timeline

import androidx.lifecycle.MutableLiveData
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
    var selectedTimelinePeriod = mutableListOf<DKTimelinePeriod>()

    val timelineDataLiveData: MutableLiveData<List<Timeline>> = MutableLiveData()

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
        selectedTimelinePeriod.addAll(DKTimelinePeriod.values())
    }

    fun fetchTimeline() {
        DriveKitDriverData.getTimelines(
            periods = selectedTimelinePeriod,
            listener = object: TimelineQueryListener {
                override fun onResponse(
                    timelineSyncStatus: TimelineSyncStatus,
                    timelines: List<Timeline>
                ) {
                    timelineDataLiveData.postValue(timelines)
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