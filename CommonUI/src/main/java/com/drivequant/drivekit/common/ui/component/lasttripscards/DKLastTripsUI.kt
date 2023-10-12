package com.drivequant.drivekit.common.ui.component.lasttripscards

import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.component.lasttripscards.fragment.DKLastTripsFragment
import com.drivequant.drivekit.common.ui.component.lasttripscards.fragment.DKLastTripsSeeMoreFragment
import com.drivequant.drivekit.common.ui.component.lasttripscards.fragment.DKLastTripsViewPagerFragment
import com.drivequant.drivekit.common.ui.component.lasttripscards.viewmodel.DKLastTripsViewModel
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay

object DKLastTripsUI {

    @JvmOverloads
    fun getLastTripWidget(
        tripsToDisplay: List<DKTripListItem>,
        headerDay: HeaderDay = HeaderDay.DISTANCE,
        tripData: TripData = TripData.SAFETY,
    ): Fragment {
        val fragments = mutableListOf<Fragment>()
        tripsToDisplay.forEach {
            val viewModel = DKLastTripsViewModel(tripData, headerDay, it)
            fragments.add(DKLastTripsFragment.newInstance(viewModel))
        }
        fragments.add(DKLastTripsSeeMoreFragment.newInstance(hasMoreTrips = tripsToDisplay.isNotEmpty()))
        return DKLastTripsViewPagerFragment.newInstance(fragments)
    }
}