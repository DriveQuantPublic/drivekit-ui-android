package com.drivequant.drivekit.timeline.ui.timeline

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.access.AccessType
import com.drivequant.drivekit.core.access.DriveKitAccess
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.driverdata.timeline.TimelineQueryListener
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.timeline.ui.DriveKitDriverDataTimelineUI

internal class TimelineViewModel : ViewModel() {

    private var selectedTimelinePeriod: DKTimelinePeriod = DKTimelinePeriod.WEEK // TODO mock

    var scores = DriveKitDriverDataTimelineUI.scoresType.toMutableList()

    var timelinePeriodTypes = listOf<DKTimelinePeriod>()

    val timelineDataLiveData: MutableLiveData<Timeline> = MutableLiveData()
    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()

    private var selectedScore: DKTimelineScoreType = scores.first()
        set(value) {
            field = value
            updateTimeline()
        }

    init {
        selectedScore = scores.firstOrNull() ?: DKTimelineScoreType.SAFETY

        timelinePeriodTypes = DKTimelinePeriod.values().toList()

        selectedTimelinePeriod = timelinePeriodTypes.first()
    }

    fun fetchTimeline() {
        DriveKitDriverData.getTimelines(
            periods = DKTimelinePeriod.values().toList(),
            listener = object : TimelineQueryListener {
                override fun onResponse(
                    timelineSyncStatus: TimelineSyncStatus,
                    timelines: List<Timeline>
                ) {
                    syncStatus.postValue(timelineSyncStatus)
                }
            },
            synchronizationType = SynchronizationType.DEFAULT
        )
    }

    private fun updateTimeline() {
        DriveKitDriverData.getTimelines(
            periods = listOf(selectedTimelinePeriod),
            listener = object : TimelineQueryListener {
                override fun onResponse(
                    timelineSyncStatus: TimelineSyncStatus,
                    timelines: List<Timeline>
                ) {
                    if (timelines.isNotEmpty()) {
                        timelineDataLiveData.postValue(
                            timelines[0]
                        )
                        //transformTimelineData(timelines[0].toTimelineData().allContext.date)
                    }
                }
            },
            synchronizationType = SynchronizationType.CACHE
        )
    }

    fun updateTimelinePeriod(period: DKTimelinePeriod) {
        if (selectedTimelinePeriod != period) {
            selectedTimelinePeriod = period
            Log.e("TEST", selectedTimelinePeriod.name)
            updateTimeline()
        }
    }

    fun updateTimelineScore(position: Int) {
        selectedScore = scores[position]
        //Log.e("TEST", selectedScore?.name? ?: DKTimelineScoreType.SAFETY)
        updateTimeline()
    }
}

//TODO (Replace with title resId)
fun DKTimelinePeriod.getTitleResId() = when(this) {
    DKTimelinePeriod.WEEK -> "Par semaine"
    DKTimelinePeriod.MONTH -> "Par mois"
}

enum class DKTimelineScoreType {
    SAFETY, ECO_DRIVING, DISTRACTION, SPEEDING;

    fun getIconResId() = when (this) {
        SAFETY -> "dk_common_safety_flat"
        DISTRACTION -> "dk_common_distraction_flat"
        ECO_DRIVING -> "dk_common_ecodriving_flat"
        SPEEDING -> "dk_common_speeding_flat"
    }

    fun hasAccess() = when (this) {
        SAFETY -> AccessType.SAFETY
        ECO_DRIVING -> AccessType.ECODRIVING
        DISTRACTION -> AccessType.PHONE_DISTRACTION
        SPEEDING -> AccessType.SPEEDING
    }.let {
        DriveKitAccess.hasAccess(it)
    }
}