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
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKRawTimeline

import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.EmptyRoadContextType
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext
import com.drivequant.drivekit.timeline.ui.distanceByRoadContext
import com.drivequant.drivekit.timeline.ui.hasData
import com.drivequant.drivekit.timeline.ui.totalDistanceForAllContexts

internal class RoadContextViewModel : ViewModel(), DKContextCard {

    val changeObserver: MutableLiveData<Any> = MutableLiveData()

    var distanceByContext = mapOf<TimelineRoadContext, Double>()
        private set(value) {
            field = value.filterNot { it.value <= 0.0 }
        }

    private var contextCards: List<DKContextCardItem> = emptyList()
    private var totalDistanceForAllContext = 0.0
    private var distance = 0.0
    private var selectedScore: DKScoreType = DKScoreType.SAFETY
    var emptyRoadContextType: EmptyRoadContextType? = null
        private set

    fun configure(timeline: DKRawTimeline?, selectedScore: DKScoreType, selectedIndex: Int?) {
        if (timeline != null && selectedIndex != null && timeline.hasData()) {
            this.selectedScore = selectedScore
            this.totalDistanceForAllContext = timeline.totalDistanceForAllContexts(selectedScore, selectedIndex)
            this.distanceByContext = timeline.distanceByRoadContext(selectedScore, selectedIndex)
            this.distance = 0.0
            this.distanceByContext.forEach {
                this.distance += it.value
            }
            this.emptyRoadContextType = if (this.distanceByContext.isEmpty()) {
                when (selectedScore) {
                    DKScoreType.DISTRACTION, DKScoreType.SPEEDING -> EmptyRoadContextType.NO_DATA
                    DKScoreType.SAFETY -> EmptyRoadContextType.NO_DATA_SAFETY
                    DKScoreType.ECO_DRIVING -> EmptyRoadContextType.NO_DATA_ECODRIVING
                }
            } else {
                null
            }
        } else {
            this.totalDistanceForAllContext = 0.0
            this.distanceByContext = emptyMap()
            this.distance = 0.0
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
            distanceByContext[roadContext]?.div(distance)?.let {
                it * 100
            } ?: run { 0.0 }
        }

    override fun getItems(): List<DKContextCardItem> = this.contextCards

    override fun getTitle(context: Context): String {
        return this.emptyRoadContextType?.let {
            when (it) {
                EmptyRoadContextType.EMPTY_DATA -> com.drivequant.drivekit.common.ui.R.string.dk_common_no_data_yet
                EmptyRoadContextType.NO_DATA_SAFETY,
                EmptyRoadContextType.NO_DATA_ECODRIVING-> R.string.dk_timeline_road_context_title_no_data
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
                EmptyRoadContextType.NO_DATA_SAFETY -> R.string.dk_timeline_road_context_description_no_data_safety
                EmptyRoadContextType.NO_DATA_ECODRIVING-> R.string.dk_timeline_road_context_description_no_data_ecodriving
                EmptyRoadContextType.NO_DATA -> R.string.dk_timeline_road_context_no_context_description
            }.let { titleResId ->
                context.getString(titleResId)
            }
        } ?: ""
    }
}

@ColorRes
internal fun TimelineRoadContext.getColorResId() = when (this) {
    TimelineRoadContext.HEAVY_URBAN_TRAFFIC -> R.color.dkRoadContextHeavyUrbanTrafficColor
    TimelineRoadContext.CITY -> R.color.dkRoadContextCityColor
    TimelineRoadContext.SUBURBAN -> R.color.dkRoadContextSuburbanColor
    TimelineRoadContext.EXPRESSWAYS -> R.color.dkRoadContextExpresswaysColor
}

@StringRes
internal fun TimelineRoadContext.getTitleResId(): Int = when (this) {
    TimelineRoadContext.HEAVY_URBAN_TRAFFIC -> R.string.dk_timeline_road_context_heavy_urban_traffic
    TimelineRoadContext.CITY -> R.string.dk_timeline_road_context_city
    TimelineRoadContext.SUBURBAN -> R.string.dk_timeline_road_context_suburban
    TimelineRoadContext.EXPRESSWAYS -> R.string.dk_timeline_road_context_expressways
}
