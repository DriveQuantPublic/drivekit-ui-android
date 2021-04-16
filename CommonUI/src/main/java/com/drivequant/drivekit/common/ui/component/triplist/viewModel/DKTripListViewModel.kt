package com.drivequant.drivekit.common.ui.component.triplist.viewModel

import com.drivequant.drivekit.common.ui.component.triplist.DKTripList
import com.drivequant.drivekit.common.ui.component.triplist.extension.orderByDay
import java.util.*

class DKTripListViewModel {

    private lateinit var tripList: DKTripList
    var sortedTrips = mutableListOf<DKTripsByDate>()

    fun setDKTripList(tripsList: DKTripList) {
        this.tripList = tripsList
    }

    fun sortTrips() {
        sortedTrips = tripList.getTripsList().orderByDay(tripList.getDayTripDescendingOrder())
    }

    fun getTripsByDate(date: Date): DKTripsByDate? {
        for (currentDKTripsByDate in sortedTrips) {
            if (currentDKTripsByDate.date == date) {
                return currentDKTripsByDate
            }
        }
        return null
    }

    fun getTripData() =  tripList.getTripData()

    fun getCustomHeader() = tripList.getCustomHeader()

    fun getHeaderDay() = tripList.getHeaderDay()
}