package com.drivequant.drivekit.common.ui.component.tripslist.viewModel

import com.drivequant.drivekit.common.ui.component.tripslist.DKTripsList
import java.util.*

class DKTripsListViewModel(val tripsList: DKTripsList) {

    fun getTripsByDate(date: Date): DKTripsByDate? {
        for (currentDKTripsByDate in tripsList.getTripsList()) {
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