package com.drivequant.drivekit.ui.tripdetail.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.drivequant.drivekit.ui.tripdetail.fragments.*
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel

class TripDetailFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    private val tripDetailViewModel : TripDetailViewModel) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return tripDetailViewModel.trip?.let { trip ->
                tripDetailViewModel.configurableMapItems[position].getFragment(trip, tripDetailViewModel)
        } ?: run {
            UnscoredTripFragment.newInstance(tripDetailViewModel.trip)
        }
    }

    override fun getCount(): Int {
        return tripDetailViewModel.configurableMapItems.size
    }
}