package com.drivequant.drivekit.tripanalysis.workinghours.viewholder

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHoursTimeSlotStatus

internal data class HoursSpinnerItem(
    private var context: Context,
    var timeSlotStatus: DKWorkingHoursTimeSlotStatus
){
    override fun toString(): String {
        when (timeSlotStatus) {
            DKWorkingHoursTimeSlotStatus.DISABLED -> "dk_working_hours_slot_mode_disabled_title"
            DKWorkingHoursTimeSlotStatus.PERSONAL -> "dk_working_hours_slot_mode_personal_title"
            DKWorkingHoursTimeSlotStatus.BUSINESS -> "dk_working_hours_slot_mode_business_title"
        }.let {
            return DKResource.convertToString(context, it)
        }
    }
}