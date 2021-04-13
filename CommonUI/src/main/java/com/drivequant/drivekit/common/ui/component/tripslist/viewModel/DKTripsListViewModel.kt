package com.drivequant.drivekit.common.ui.component.tripslist.viewModel

import com.drivequant.drivekit.common.ui.component.tripslist.DKTripsList
import com.drivequant.drivekit.common.ui.component.tripslist.extension.orderByDay
import java.util.*

class DKTripsListViewModel {

    private lateinit var tripsList: DKTripsList
    var sortedTrips = mutableListOf<DKTripsByDate>()

    fun setDKTripsList(tripsList: DKTripsList) {
        this.tripsList = tripsList
    }

    fun sortTrips() {
        sortedTrips = tripsList.getTripsList().orderByDay(tripsList.getDayTripDescendingOrder())
    }

    fun getTripsByDate(date: Date): DKTripsByDate? {
        for (currentDKTripsByDate in sortedTrips) {
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