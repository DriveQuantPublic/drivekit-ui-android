package com.drivequant.drivekit.tripanalysis.workinghours.viewholder

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.service.workinghours.TripStatus

internal data class HoursSpinnerItem(
    private var context: Context,
    var tripStatus: TripStatus
){
    override fun toString(): String {
        when (tripStatus) {
            TripStatus.DISABLED -> "dk_working_hours_slot_mode_disabled_title"
            TripStatus.PERSONAL -> "dk_working_hours_slot_mode_personal_title"
            TripStatus.BUSINESS -> "dk_working_hours_slot_mode_business_title"
        }.let {
            return DKResource.convertToString(context, it)
        }
    }
}