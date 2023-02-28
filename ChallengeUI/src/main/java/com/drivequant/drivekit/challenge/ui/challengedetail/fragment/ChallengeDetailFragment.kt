package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.ChallengeUI
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.adapter.ChallengeDetailFragmentPagerAdapter
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.dk_fragment_challenge_detail.*


class ChallengeDetailFragment : Fragment() {

    private lateinit var challengeId: String
    private lateinit var viewModel: ChallengeDetailViewModel
    private lateinit var startSyncType: SynchronizationType
    private var shouldSyncDetail = true

    companion object {
        fun newInstance(challengeId: String): ChallengeDetailFragment {
            val fragment = ChallengeDetailFragment()
            fragment.challengeId = challengeId
            return fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::challengeId.isInitialized) {
            outState.putString("challengeIdTag", challengeId)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_challenge_detail, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_challenge_detail"
            ), javaClass.simpleName
        )

        (savedInstanceState?.getString("challengeIdTag"))?.let { it ->
            challengeId = it
        }
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(challengeId)
            ).get(ChallengeDetailViewModel::class.java)
        }
        startSyncType = if (viewModel.getLocalChallengeDetail() != null) SynchronizationType.CACHE else SynchronizationType.DEFAULT
        viewModel.syncChallengeDetailError.observe(viewLifecycleOwner) {
            if (!it) {
                Toast.makeText(
                    requireContext(),
                    DKResource.convertToString(
                        requireContext(),
                        "dk_challenge_score_alert_message"
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
            setViewPager()
            updateProgressVisibility(false)

            // If user has data in local database then load them first, then fetch detail challenge
            if (startSyncType == SynchronizationType.CACHE && shouldSyncDetail) {
                shouldSyncDetail = false
                updateChallengeDetail(SynchronizationType.DEFAULT)
            }
        }
        updateChallengeDetail(startSyncType)
    }

    private fun updateChallengeDetail(synchronizationType: SynchronizationType) {
        updateProgressVisibility(true)
        viewModel.fetchChallengeDetail(synchronizationType)
    }

    private fun setViewPager() {
        view_pager_challenge_detail.offscreenPageLimit = ChallengeUI.challengeDetailItems.size
        view_pager_challenge_detail.adapter = ChallengeDetailFragmentPagerAdapter(
            childFragmentManager,
            viewModel
        )

        tab_layout_challenge_detail.setupWithViewPager(view_pager_challenge_detail)
        for ((index, item) in ChallengeUI.challengeDetailItems.withIndex()) {
            tab_layout_challenge_detail?.getTabAt(index)?.let {
                val drawable = ContextCompat.getDrawable(requireContext(), item.getImageResource())
                if (index == 0) {
                    drawable?.tintDrawable(DriveKitUI.colors.secondaryColor())
                }
                it.icon = drawable
            }
        }
        tab_layout_challenge_detail.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateTabLayout(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun updateTabLayout(position: Int) {
        for (i in 0 until tab_layout_challenge_detail.tabCount) {
            tab_layout_challenge_detail.getTabAt(i)?.let {
                if (i == position) {
                    val drawable = ContextCompat.getDrawable(requireContext(),
                        ChallengeUI.challengeDetailItems[i].getImageResource())?.mutate()
                    drawable?.tintDrawable(DriveKitUI.colors.secondaryColor())
                    tab_layout_challenge_detail.getTabAt(i)?.icon = drawable
                } else {
                    val drawable = ContextCompat.getDrawable(requireContext(),
                        ChallengeUI.challengeDetailItems[i].getImageResource())?.mutate()
                    drawable?.tintDrawable(DriveKitUI.colors.complementaryFontColor())
                    tab_layout_challenge_detail.getTabAt(i)?.icon = drawable
                }
            }
        }
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        progress_circular?.apply {
            visibility = if (displayProgress) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}
