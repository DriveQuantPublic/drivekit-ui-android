package com.drivequant.drivekit.ui.tripdetail.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.drivequant.drivekit.ui.tripdetail.fragments.*
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel

class TripDetailFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    private val tripDetailViewModel: TripDetailViewModel) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return tripDetailViewModel.configurableMapItems[position].getFragment(
            tripDetailViewModel.trip,
            tripDetailViewModel
        )?.let {
            val shouldSetFragmentArgument = tripDetailViewModel.configurableMapItems[position] !is MapItem
            if (shouldSetFragmentArgument) {
                val bundle = Bundle()
                bundle.putSerializable("trip", tripDetailViewModel.trip)
                it.arguments = bundle
            }
            it
        } ?: run {
            UnscoredTripFragment.newInstance(tripDetailViewModel.trip)
        }
    }

    override fun getCount(): Int = tripDetailViewModel.configurableMapItems.size
}