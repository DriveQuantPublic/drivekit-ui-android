package com.drivequant.drivekit.common.ui.component.lasttrips

import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.component.lasttrips.fragment.DKLastTripsFragment
import com.drivequant.drivekit.common.ui.component.lasttrips.fragment.DKLastTripsViewPagerFragment
import com.drivequant.drivekit.common.ui.component.lasttrips.viewmodel.DKLastTripsViewModel
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay

object DKLastTripsUI {

    fun getLastTripWidget(
        trips: List<DKTripListItem>,
        headerDay: HeaderDay = HeaderDay.DISTANCE,
        tripData: TripData = TripData.SAFETY): Fragment {
        val fragments = mutableListOf<DKLastTripsFragment>()
        trips.forEach {
            val viewModel = DKLastTripsViewModel(tripData, headerDay, it)
            fragments.add(
                DKLastTripsFragment.newInstance(viewModel)
            )
        }
        fragments.add(
            DKLastTripsFragment()
        )
        return DKLastTripsViewPagerFragment.newInstance(fragments)
    }
}