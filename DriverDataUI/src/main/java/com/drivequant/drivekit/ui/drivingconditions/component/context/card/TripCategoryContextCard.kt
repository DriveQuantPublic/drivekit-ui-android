package com.drivequant.drivekit.ui.drivingconditions.component.context.card

import android.content.Context
import androidx.annotation.ColorRes
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.ui.R

internal class TripCategoryContextCard(
    private val context: Context,
    private val drivingConditions: DKDriverTimeline.DKDrivingConditions
) : BaseContextCard() {

    private var items: List<DKContextCardItem>? = null

    override fun getItems(): List<DKContextCardItem> {
        this.items?.let {
            return it
        }
        val numberOfTrips = this.drivingConditions.tripCountByCategory.values.sum().toDouble()
        val cards = DKDriverTimeline.DKDrivingCategory.values().mapNotNull { category ->
            drivingConditions.tripCountByCategory[category]?.let {
                if (it > 0) {
                    getContextCardItem(
                        category.getTitle(context),
                        category.getColor(),
                        it.toDouble(),
                        numberOfTrips,
                        UnitKind.TRIP
                    )
                } else {
                    null
                }
            }
        }
        return cards.also {
            this.items = it
        }
    }

    override fun getTitle(context: Context): String {
        val maxCategory = getMaxKey(this.drivingConditions.tripCountByCategory)
        return if (maxCategory == null) {
            ""
        } else {
            when (maxCategory) {
                DKDriverTimeline.DKDrivingCategory.LESS_THAN_2_KM -> context.getString(R.string.dk_driverdata_drivingconditions_main_short_trips)
                DKDriverTimeline.DKDrivingCategory.FROM_2_TO_10_KM -> context.getString(R.string.dk_driverdata_drivingconditions_main_interval_distance, 2, 10)
                DKDriverTimeline.DKDrivingCategory.FROM_10_TO_50_KM -> context.getString(R.string.dk_driverdata_drivingconditions_main_interval_distance, 10, 50)
                DKDriverTimeline.DKDrivingCategory.FROM_50_TO_100_KM -> context.getString(R.string.dk_driverdata_drivingconditions_main_interval_distance, 50, 100)
                DKDriverTimeline.DKDrivingCategory.MORE_THAN_100_KM -> context.getString(R.string.dk_driverdata_drivingconditions_main_long_trips)
            }
        }
    }
}

private fun DKDriverTimeline.DKDrivingCategory.getTitle(context: Context): String = when (this) {
    DKDriverTimeline.DKDrivingCategory.LESS_THAN_2_KM -> context.getString(R.string.dk_driverdata_drivingconditions_short_trips)
    DKDriverTimeline.DKDrivingCategory.FROM_2_TO_10_KM -> context.getString(R.string.dk_driverdata_drivingconditions_interval_distance, 2, 10)
    DKDriverTimeline.DKDrivingCategory.FROM_10_TO_50_KM -> context.getString(R.string.dk_driverdata_drivingconditions_interval_distance, 10, 50)
    DKDriverTimeline.DKDrivingCategory.FROM_50_TO_100_KM -> context.getString(R.string.dk_driverdata_drivingconditions_interval_distance, 50, 100)
    DKDriverTimeline.DKDrivingCategory.MORE_THAN_100_KM -> context.getString(R.string.dk_driverdata_drivingconditions_long_trips)
}

@ColorRes
private fun DKDriverTimeline.DKDrivingCategory.getColor(): Int = when (this) {
    DKDriverTimeline.DKDrivingCategory.LESS_THAN_2_KM -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor1
    DKDriverTimeline.DKDrivingCategory.FROM_2_TO_10_KM -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor2
    DKDriverTimeline.DKDrivingCategory.FROM_10_TO_50_KM -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor3
    DKDriverTimeline.DKDrivingCategory.FROM_50_TO_100_KM -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor4
    DKDriverTimeline.DKDrivingCategory.MORE_THAN_100_KM -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor5
}
