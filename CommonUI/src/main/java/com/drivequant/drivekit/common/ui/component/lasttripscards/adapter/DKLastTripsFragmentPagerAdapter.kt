package com.drivequant.drivekit.common.ui.component.lasttripscards.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.drivequant.drivekit.common.ui.component.lasttripscards.fragment.DKLastTripsFragment

internal class DKLastTripsFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    val fragments: List<DKLastTripsFragment>
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size
}