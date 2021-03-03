package com.drivequant.drivekit.ui.tripdetail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.drivequant.drivekit.ui.tripdetail.fragments.*
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel

internal class TripDetailFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    private val tripDetailViewModel: TripDetailViewModel) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return tripDetailViewModel.configurableMapItems[position].getFragment(
            tripDetailViewModel.trip,
            tripDetailViewModel
        ) ?: run {
            UnscoredTripFragment.newInstance(tripDetailViewModel.trip)
        }
    }

    override fun getCount(): Int = tripDetailViewModel.configurableMapItems.size
}