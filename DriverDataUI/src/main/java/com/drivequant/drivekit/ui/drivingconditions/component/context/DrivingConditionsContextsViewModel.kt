package com.drivequant.drivekit.ui.drivingconditions.component.context

import android.content.Context
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCard
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.ui.drivingconditions.component.context.card.*
import com.drivequant.drivekit.ui.drivingconditions.component.context.card.DayNightContextCard
import com.drivequant.drivekit.ui.drivingconditions.component.context.card.RoadContextCard
import com.drivequant.drivekit.ui.drivingconditions.component.context.card.TripDistanceContextCard
import com.drivequant.drivekit.ui.drivingconditions.component.context.card.WeatherContextCard

internal class DrivingConditionsContextsViewModel {

    var contextCards: List<DKContextCard> = listOf()
        private set

    var onViewModelUpdate: (() -> Unit)? = null

    fun configure(context: Context, contextKinds: List<DKContextKind>, drivingConditions: DKDriverTimeline.DKDrivingConditions, roadContexts: Map<RoadContext, DKDriverTimeline.DKRoadContextItem>) {
        this.contextCards = contextKinds.map { contextKind ->
            when(contextKind) {
                DKContextKind.DAY_NIGHT -> DayNightContextCard(context, drivingConditions)
                DKContextKind.ROAD -> RoadContextCard(context, roadContexts)
                DKContextKind.TRIP_DISTANCE -> TripDistanceContextCard(context, drivingConditions)
                DKContextKind.WEATHER -> WeatherContextCard(context, drivingConditions)
                DKContextKind.WEEK -> WeekContextCard(context, drivingConditions)
            }
        }
        this.onViewModelUpdate?.invoke()
    }

}
