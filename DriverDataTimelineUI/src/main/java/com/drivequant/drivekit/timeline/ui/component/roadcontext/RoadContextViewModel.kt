package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCard
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.EmptyRoadContextType
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.toTimelineRoadContext
import java.util.Date

internal class RoadContextViewModel : ViewModel(), DKContextCard {

    val changeObserver: MutableLiveData<Any> = MutableLiveData()

    private var distanceByContext = mapOf<TimelineRoadContext, Double>()
    private var contextCards: List<DKContextCardItem> = emptyList()
    private var totalDistanceForAllContext = 0.0
    private var emptyRoadContextType: EmptyRoadContextType? = null

    fun configure(timeline: DKDriverTimeline?, selectedDate: Date?) {
        if (timeline != null && selectedDate != null) {
            this.distanceByContext = buildMap {
                timeline.roadContexts.forEach { roadContextItem ->
                    val distance = roadContextItem.value.firstOrNull { it.date == selectedDate }?.distance ?: 0.0
                    if (distance > 0) {
                        this[roadContextItem.key.toTimelineRoadContext()] = distance
                    }
                }
            }

            this.totalDistanceForAllContext = timeline.allContext.firstOrNull { it.date == selectedDate }?.distance ?: 0.0

            this.emptyRoadContextType = if (this.distanceByContext.isEmpty()) {
                EmptyRoadContextType.NO_DATA
            } else {
                null
            }
        } else {
            this.totalDistanceForAllContext = 0.0
            this.distanceByContext = emptyMap()
            this.emptyRoadContextType = EmptyRoadContextType.EMPTY_DATA
        }
        updateContextCards()
        this.changeObserver.postValue(Any())
    }

    private fun updateContextCards() {
        this.contextCards = if (this.distanceByContext.isEmpty()) {
            emptyList()
        } else {
            val contexts = listOf(TimelineRoadContext.HEAVY_URBAN_TRAFFIC, TimelineRoadContext.CITY, TimelineRoadContext.SUBURBAN, TimelineRoadContext.EXPRESSWAYS)
            contexts.mapNotNull { roadContext ->
                val percent = getPercent(roadContext)
                if (percent <= 0) {
                    null
                } else {
                    object : DKContextCardItem {
                        override fun getColorResId(): Int = roadContext.getColorResId()
                        override fun getTitle(context: Context): String = context.getString(roadContext.getTitleResId())
                        override fun getSubtitle(context: Context): String? = null
                        override fun getPercent(): Double = percent
                    }
                }
            }
        }
    }

    fun displayData() = emptyRoadContextType == null

    private fun formatDistanceInKm(context: Context): String {
        return DKDataFormatter.formatMeterDistanceInKm(
            context = context,
            distance = totalDistanceForAllContext * 1000,
            minDistanceToRemoveFractions = 10.0
        ).convertToString()
    }

    private fun getPercent(roadContext: TimelineRoadContext) =
        if (distanceByContext.isEmpty()) {
            0.0
        } else {
            distanceByContext[roadContext]?.div(this.totalDistanceForAllContext)?.let {
                it * 100
            } ?: run { 0.0 }
        }

    override fun getItems(): List<DKContextCardItem> = this.contextCards

    override fun getTitle(context: Context): String {
        return this.emptyRoadContextType?.let {
            when (it) {
                EmptyRoadContextType.EMPTY_DATA -> com.drivequant.drivekit.common.ui.R.string.dk_common_no_data_yet
                EmptyRoadContextType.NO_DATA -> null
            }?.let { titleResId ->
                context.getString(titleResId)
            } ?: ""
        } ?: context.getString(R.string.dk_timeline_road_context_title, formatDistanceInKm(context))
    }

    override fun getEmptyDataDescription(context: Context): String {
        return this.emptyRoadContextType?.let {
            when (it) {
                EmptyRoadContextType.EMPTY_DATA -> R.string.dk_timeline_road_context_description_empty_data
                EmptyRoadContextType.NO_DATA -> R.string.dk_timeline_road_context_no_context_description
            }.let { titleResId ->
                context.getString(titleResId)
            }
        } ?: ""
    }
}

@ColorRes
internal fun TimelineRoadContext.getColorResId() = when (this) {
    TimelineRoadContext.HEAVY_URBAN_TRAFFIC -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor1
    TimelineRoadContext.CITY -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor2
    TimelineRoadContext.SUBURBAN -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor3
    TimelineRoadContext.EXPRESSWAYS -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor4
}

@StringRes
internal fun TimelineRoadContext.getTitleResId(): Int = when (this) {
    TimelineRoadContext.HEAVY_URBAN_TRAFFIC -> R.string.dk_timeline_road_context_heavy_urban_traffic
    TimelineRoadContext.CITY -> R.string.dk_timeline_road_context_city
    TimelineRoadContext.SUBURBAN -> R.string.dk_timeline_road_context_suburban
    TimelineRoadContext.EXPRESSWAYS -> R.string.dk_timeline_road_context_expressways
}
