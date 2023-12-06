package com.drivequant.drivekit.challenge.ui.challengelist.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.challengelist.adapter.ChallengesFragmentPagerAdapter
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeListViewModel
import com.drivequant.drivekit.challenge.ui.databinding.DkFragmentChallengeBinding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.updateTabsFont
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType


class ChallengeFragment : Fragment() {

    private lateinit var viewModel: ChallengeListViewModel
    private var _binding: DkFragmentChallengeBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DkFragmentChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[ChallengeListViewModel::class.java]
        }
        updateProgressVisibility(true)
        viewModel.fetchChallengeList()
        viewModel.syncChallengesError.observe(viewLifecycleOwner) {
            if (!it) {
                Toast.makeText(
                    context,
                    DKResource.convertToString(
                        requireContext(),
                        "dk_challenge_failed_to_sync_challenges"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
            viewModel.filterChallenges()
            updateProgressVisibility(false)
        }
        setViewPager()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateChallenge() {
        if (this::viewModel.isInitialized) {
            viewModel.fetchChallengeList(SynchronizationType.CACHE)
        }
    }

    private fun setViewPager() {
        binding.viewPagerChallenge.adapter =
            ChallengesFragmentPagerAdapter(
                childFragmentManager,
                viewModel, requireContext()
            )
        binding.tabLayoutChallenge.apply {
            setupWithViewPager(binding.viewPagerChallenge)
            setBackgroundColor(Color.WHITE)
            setTabTextColors(
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.secondaryColor()
            )
            updateTabsFont()
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
