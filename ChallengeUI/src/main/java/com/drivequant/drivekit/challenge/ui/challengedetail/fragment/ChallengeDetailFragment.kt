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
import com.drivequant.drivekit.challenge.ui.databinding.DkFragmentChallengeDetailBinding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.tint
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.core.SynchronizationType
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class ChallengeDetailFragment : Fragment() {

    private lateinit var challengeId: String
    private lateinit var viewModel: ChallengeDetailViewModel
    private lateinit var startSyncType: SynchronizationType
    private var shouldSyncDetail = true
    private var _binding: DkFragmentChallengeDetailBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

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
    ): View {
        _binding = DkFragmentChallengeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_challenge_detail), javaClass.simpleName)

        (savedInstanceState?.getString("challengeIdTag"))?.let {
            challengeId = it
        }
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(challengeId)
            )[ChallengeDetailViewModel::class.java]
        }
        startSyncType = if (viewModel.getLocalChallengeDetail() != null) SynchronizationType.CACHE else SynchronizationType.DEFAULT
        viewModel.syncChallengeDetailError.observe(viewLifecycleOwner) {
            if (!it) {
                Toast.makeText(
                    requireContext(),
                    R.string.dk_challenge_score_alert_message,
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateChallengeDetail(synchronizationType: SynchronizationType) {
        updateProgressVisibility(true)
        viewModel.fetchChallengeDetail(synchronizationType)
    }

    private fun setViewPager() {
        binding.viewPagerChallengeDetail.offscreenPageLimit = ChallengeUI.challengeDetailItems.size
        binding.viewPagerChallengeDetail.adapter = ChallengeDetailFragmentPagerAdapter(
            childFragmentManager,
            viewModel
        )

        binding.tabLayoutChallengeDetail.setupWithViewPager(binding.viewPagerChallengeDetail)
        val context = requireContext()
        for ((index, item) in ChallengeUI.challengeDetailItems.withIndex()) {
            binding.tabLayoutChallengeDetail.getTabAt(index)?.let {
                val drawable = ContextCompat.getDrawable(context, item.getImageResource())
                if (index == 0) {
                    drawable?.tint(context, com.drivequant.drivekit.common.ui.R.color.secondaryColor)
                }
                it.icon = drawable
            }
        }
        binding.tabLayoutChallengeDetail.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateTabLayout(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun updateTabLayout(position: Int) {
        context?.let { context ->
            for (i in 0 until binding.tabLayoutChallengeDetail.tabCount) {
                binding.tabLayoutChallengeDetail.getTabAt(i)?.let {
                    val drawable = ContextCompat.getDrawable(context, ChallengeUI.challengeDetailItems[i].getImageResource())
                    val color = if (i == position) {
                        DKColors.secondaryColor
                    } else {
                        DKColors.complementaryFontColor
                    }
                    drawable?.tintDrawable(color)
                    it.icon = drawable
                }
            }
        }
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        binding.progressCircular.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
