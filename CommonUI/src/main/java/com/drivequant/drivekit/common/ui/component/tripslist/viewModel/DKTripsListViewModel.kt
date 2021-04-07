package com.drivequant.drivekit.common.ui.component.tripslist.viewModel

import com.drivequant.drivekit.common.ui.component.tripslist.DKTripsByDate
import java.util.*

class DKTripsListViewModel(val tripsList: List<DKTripsByDate>) {

    fun getTripsByDate(date: Date): DKTripsByDate? {
        for (currentDKTripsByDate in tripsList) {
            if (currentDKTripsByDate.date == date) {
                return currentDKTripsByDate
            }
        }
        return null
    }
}