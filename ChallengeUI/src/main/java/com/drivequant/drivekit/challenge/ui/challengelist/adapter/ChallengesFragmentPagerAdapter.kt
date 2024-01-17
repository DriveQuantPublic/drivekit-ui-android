package com.drivequant.drivekit.challenge.ui.challengelist.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengelist.fragment.ChallengeListFragment
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeListViewModel
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus

internal class ChallengesFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    challengeListViewModel: ChallengeListViewModel,
    val context: Context
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = listOf<Fragment>(
        ChallengeListFragment.newInstance(
            listOf(ChallengeStatus.PENDING, ChallengeStatus.SCHEDULED),
            challengeListViewModel
        ), ChallengeListFragment.newInstance(
            listOf(ChallengeStatus.FINISHED, ChallengeStatus.ARCHIVED),
            challengeListViewModel
        )
    )

    private val titles = listOf(R.string.dk_challenge_active, R.string.dk_challenge_finished)

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getPageTitle(position: Int): CharSequence = context.getString(titles[position])

    override fun getCount(): Int = fragments.size
}
