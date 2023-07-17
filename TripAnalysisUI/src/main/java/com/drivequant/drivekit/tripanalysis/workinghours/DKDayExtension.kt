package com.drivequant.drivekit.tripanalysis.workinghours

import com.drivequant.drivekit.core.common.DKDay
import java.util.*

fun DKDay.toDay(): Int {
    return when (this) {
        DKDay.MONDAY -> Calendar.MONDAY
        DKDay.TUESDAY -> Calendar.TUESDAY
        DKDay.WEDNESDAY -> Calendar.WEDNESDAY
        DKDay.THURSDAY -> Calendar.THURSDAY
        DKDay.FRIDAY -> Calendar.FRIDAY
        DKDay.SATURDAY -> Calendar.SATURDAY
        DKDay.SUNDAY -> Calendar.SUNDAY
    }
}
