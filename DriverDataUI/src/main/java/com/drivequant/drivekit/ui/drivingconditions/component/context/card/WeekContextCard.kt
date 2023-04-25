package com.drivequant.drivekit.ui.drivingconditions.component.context.card

import android.content.Context
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.drivingconditions.component.context.DrivingConditionsContextsViewModel

internal class WeekContextCard(
    private val context: Context,
    private val drivingConditions: DKDriverTimeline.DKDrivingConditions
) : BaseContextCard() {

    private var items: List<DKContextCardItem>? = null

    override fun getItems(): List<DKContextCardItem> {
        this.items?.let {
            return it
        }
        val totalDistance = drivingConditions.weekdaysDistance + drivingConditions.weekendDistance
        val weekdaysCard = if (drivingConditions.weekdaysDistance > 0) {
            getContextCardItem(
                context.getString(R.string.dk_driverdata_drivingconditions_weekdays),
                com.drivequant.drivekit.common.ui.R.color.dkContextCardColor1,
                drivingConditions.weekdaysDistance,
                totalDistance,
                UnitKind.KILOMETER
            )
        } else {
            null
        }
        val weekendCard = if (drivingConditions.weekendDistance > 0) {
            getContextCardItem(
                context.getString(R.string.dk_driverdata_drivingconditions_weekend),
                com.drivequant.drivekit.common.ui.R.color.dkContextCardColor2,
                drivingConditions.weekendDistance,
                totalDistance,
                UnitKind.KILOMETER
            )
        } else {
            null
        }
        return listOfNotNull(weekdaysCard, weekendCard).also {
            this.items = it
        }
    }

    override fun getTitle(context: Context): String {
        val ratio = if (drivingConditions.weekendDistance <= 0) Double.MAX_VALUE else drivingConditions.weekdaysDistance / drivingConditions.weekendDistance
        return when {
            ratio < LESS_THAN_BOUND -> R.string.dk_driverdata_drivingconditions_main_weekend
            ratio > GREATER_THAN_BOUND -> R.string.dk_driverdata_drivingconditions_main_weekdays
            else -> R.string.dk_driverdata_drivingconditions_all_week
        }.let {
            context.getString(it)
        }
    }
}
