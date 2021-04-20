package com.drivequant.drivekit.ui.synthesiscards.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardFragment

internal class DKSynthesisCardFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    val fragments: List<DKSynthesisCardFragment>
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size
}