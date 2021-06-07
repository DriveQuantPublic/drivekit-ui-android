package com.drivequant.drivekit.challenge.ui.challengedetail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.drivequant.drivekit.challenge.ui.ChallengeUI
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel

internal class ChallengeDetailFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    private val viewModel: ChallengeDetailViewModel) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment =
        ChallengeUI.challengeDetailItems[position].getFragment(viewModel)

    override fun getCount(): Int = ChallengeUI.challengeDetailItems.size
}