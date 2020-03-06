package com.drivequant.drivekit.ui.tripdetail.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.drivequant.drivekit.ui.tripdetail.fragments.*
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel

class TripDetailFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    private val tripDetailViewModel : TripDetailViewModel) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return tripDetailViewModel.trip?.let {
             when(tripDetailViewModel.configurableMapItems[position]){
                MapItem.SAFETY -> SafetyFragment.newInstance(it.safety!!)
                MapItem.ECO_DRIVING -> EcoDrivingFragment.newInstance(it.ecoDriving!!)
                MapItem.DISTRACTION -> DriverDistractionFragment.newInstance(it.driverDistraction!!)
                MapItem.INTERACTIVE_MAP -> TripTimelineFragment.newInstance(tripDetailViewModel)
                MapItem.SYNTHESIS -> SynthesisFragment.newInstance(it)
            }
        } ?: run {
            UnscoredTripFragment.newInstance(tripDetailViewModel.trip)
        }
    }

    override fun getCount(): Int {
        return tripDetailViewModel.configurableMapItems.size
    }
}