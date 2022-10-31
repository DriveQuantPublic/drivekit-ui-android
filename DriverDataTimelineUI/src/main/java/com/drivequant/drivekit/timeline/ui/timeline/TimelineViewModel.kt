package com.drivequant.drivekit.timeline.ui.timeline

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.driverdata.timeline.TimelineQueryListener
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.timeline.ui.DKTimelineScoreType
import com.drivequant.drivekit.timeline.ui.DriveKitDriverDataTimelineUI

internal class TimelineViewModel : ViewModel() {

    var scores: List<DKTimelineScoreType> = DriveKitDriverDataTimelineUI.scores.toMutableList()

    var timelinePeriodTypes = DKTimelinePeriod.values().toList()
    private var selectedTimelinePeriod: DKTimelinePeriod = timelinePeriodTypes.first()

    val timelineDataLiveData: MutableLiveData<Timeline> = MutableLiveData()
    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()

    private var selectedScore: DKTimelineScoreType = scores.first()
        set(value) {
            field = value
            updateTimeline()
        }

    private var weekTimeline: Timeline? = null
    private var monthTimeline: Timeline? = null

    init {
        // periodSelectorViewModel.listener = this
        DriveKitDriverData.getTimelines(DKTimelinePeriod.values().asList(), object : TimelineQueryListener {
            override fun onResponse(
                timelineSyncStatus: TimelineSyncStatus,
                timelines: List<Timeline>
            ) {
                if (timelineSyncStatus == TimelineSyncStatus.CACHE_DATA_ONLY) {
                    timelines.forEach {
                        when (it.period) {
                            TimelinePeriod.WEEK -> weekTimeline = it
                            TimelinePeriod.MONTH -> monthTimeline = it
                        }
                    }
                    update()
                }
            }

        }, SynchronizationType.CACHE)
    }

    fun updateTimeline() {
        DriveKitDriverData.getTimelines(DKTimelinePeriod.values().asList(), object : TimelineQueryListener {
            override fun onResponse(
                timelineSyncStatus: TimelineSyncStatus,
                timelines: List<Timeline>
            ) {
                if (timelineSyncStatus != TimelineSyncStatus.NO_TIMELINE_YET) {
                    timelines.forEach {
                        when (it.period) {
                            TimelinePeriod.WEEK -> weekTimeline = it
                            TimelinePeriod.MONTH -> monthTimeline = it
                        }
                    }
                }
                syncStatus.postValue(timelineSyncStatus)
                // TODO
                update()
            }
        })
    }

    private fun update() {
        lateinit var timelineSource: Timeline
        // TODO next ticket
    }

    fun updateTimelinePeriod(period: DKTimelinePeriod) {
        if (selectedTimelinePeriod != period) {
            selectedTimelinePeriod = period
            Log.e("TEST", selectedTimelinePeriod.name)
            update()
        }
    }

    fun updateTimelineScore(position: Int) {
        selectedScore = scores[position]
        //Log.e("TEST", selectedScore?.name? ?: DKTimelineScoreType.SAFETY)
        update()
    }
}

//TODO (Replace with title resId)
fun DKTimelinePeriod.getTitleResId() = when(this) {
    DKTimelinePeriod.WEEK -> "Par semaine"
    DKTimelinePeriod.MONTH -> "Par mois"
}

