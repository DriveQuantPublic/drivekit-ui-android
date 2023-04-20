package com.drivequant.drivekit.ui.drivingconditions.component.context.card

import android.content.Context
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.ui.R

internal class DayNightContextCard(
    private val context: Context,
    private val drivingConditions: DKDriverTimeline.DKDrivingConditions
) : BaseContextCard() {

    private var items: List<DKContextCardItem>? = null

    override fun getItems(): List<DKContextCardItem> {
        this.items?.let {
            return it
        }
        val totalDistance = drivingConditions.dayDistance + drivingConditions.nightDistance
        val dayCard = if (drivingConditions.dayDistance > 0) {
            getContextCardItem(
                context.getString(R.string.dk_driverdata_drivingconditions_day),
                com.drivequant.drivekit.common.ui.R.color.dkContextCardColor1,
                drivingConditions.dayDistance,
                totalDistance
            )
        } else {
            null
        }
        val nightCard = if (drivingConditions.nightDistance > 0) {
            getContextCardItem(
                context.getString(R.string.dk_driverdata_drivingconditions_night),
                com.drivequant.drivekit.common.ui.R.color.dkContextCardColor2,
                drivingConditions.nightDistance,
                totalDistance
            )
        } else {
            null
        }
        return listOfNotNull(dayCard, nightCard).also {
            this.items = it
        }
    }

    override fun getTitle(context: Context): String {
        val ratio = if (drivingConditions.nightDistance <= 0) Double.MAX_VALUE else drivingConditions.dayDistance / drivingConditions.nightDistance
        return when {
            ratio < LESS_THAN_BOUND -> R.string.dk_driverdata_drivingconditions_main_night
            ratio > GREATER_THAN_BOUND -> R.string.dk_driverdata_drivingconditions_main_day
            else -> R.string.dk_driverdata_drivingconditions_all_day
        }.let {
            context.getString(it)
        }
    }
}
