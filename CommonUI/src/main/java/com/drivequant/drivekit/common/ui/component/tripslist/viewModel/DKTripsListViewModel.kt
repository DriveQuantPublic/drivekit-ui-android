package com.drivequant.drivekit.common.ui.component.tripslist.viewModel

import com.drivequant.drivekit.common.ui.component.tripslist.DKTripsList
import com.drivequant.drivekit.common.ui.component.tripslist.extension.orderByDay
import java.util.*

class DKTripsListViewModel(private val tripsList: DKTripsList) {

    fun getDKTripsByDate() = tripsList.getTripsList().orderByDay(tripsList.getDayTripDescendingOrder())

    fun getTripsByDate(date: Date): DKTripsByDate? {
        val tripsByDate = getDKTripsByDate()
        for (currentDKTripsByDate in tripsByDate) {
            if (currentDKTripsByDate.date == date) {
                return currentDKTripsByDate
            }
        }
        return null
    }

    fun getTripData() =  tripsList.getTripData()

    fun getCustomHeader() = tripsList.getCustomHeader()

    fun getHeaderDay() = tripsList.getHeaderDay()
}