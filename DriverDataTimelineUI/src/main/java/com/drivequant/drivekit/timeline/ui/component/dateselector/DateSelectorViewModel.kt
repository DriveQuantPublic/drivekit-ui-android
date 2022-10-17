package com.drivequant.drivekit.timeline.ui.component.dateselector

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import java.util.*

internal class DateSelectorViewModel: ViewModel() {

    private var dates : List<Date> = mutableListOf()
    private var period = DKTimelinePeriod.MONTH
    private var selectedDateIndex: Int = 0

    var listener : DateSelectorListener? = null
    var hasPreviousDate : Boolean = false
    var hasNextDate: Boolean = false
    var fromDate: Date? = null
    var toDate: Date? = null

    fun update(dates : List<Date>) {

    }

    fun showDate(directionType: DirectionType) {
        when (directionType) {
            DirectionType.NEXT -> TODO()
            DirectionType.PREVIOUS -> TODO()
        }
    }
}

enum class DirectionType {
    NEXT, PREVIOUS
}