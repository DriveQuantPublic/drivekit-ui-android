package com.drivequant.drivekit.ui.tripdetail.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.TripsViewConfig
import com.drivequant.drivekit.ui.tripdetail.fragments.*
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel

class TripDetailFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    private val tripDetailViewModel : TripDetailViewModel,
    private val tripDetailViewConfig: TripDetailViewConfig,
    private val tripsViewConfig: TripsViewConfig
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return tripDetailViewModel.trip?.let {
             when(tripDetailViewModel.configurableMapItems[position]){
                MapItem.SAFETY -> SafetyFragment.newInstance(it.safety!!, tripDetailViewConfig, tripsViewConfig)
                MapItem.ECO_DRIVING -> EcoDrivingFragment.newInstance(it.ecoDriving!!, tripDetailViewConfig, tripsViewConfig)
                MapItem.DISTRACTION -> DriverDistractionFragment.newInstance(it.driverDistraction!!, tripDetailViewConfig, tripsViewConfig)
                MapItem.INTERACTIVE_MAP -> TripTimelineFragment.newInstance(tripDetailViewModel, tripsViewConfig, tripDetailViewConfig)
            }
        } ?: run {
            UnscoredTripFragment.newInstance(tripDetailViewModel.trip, tripsViewConfig, tripDetailViewConfig)
        }
    }

    override fun getCount(): Int {
        return tripDetailViewModel.configurableMapItems.size
    }
}