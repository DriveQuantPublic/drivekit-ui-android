package com.drivequant.drivekit.challenge.ui.challengelist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengelist.model.ChallengeListCategory
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeListViewModel
import com.drivequant.drivekit.challenge.ui.databinding.DkFragmentChallengeBinding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorView
import com.drivequant.drivekit.core.SynchronizationType
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


// TODO rename to ChallengeListFragment ?
class ChallengeFragment : Fragment() {

    private lateinit var viewModel: ChallengeListViewModel
    private var _binding: DkFragmentChallengeBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    private lateinit var dateSelectorView: DKDateSelectorView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DkFragmentChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkViewModelInitialization()

        setupSwipeToRefresh()
        configureTabSelectorView()
        configureDateSelector()

        this.viewModel.updateData.observe(viewLifecycleOwner) {
            updateDateSelector()
            //updateChallengeList() //TODO
        }

        this.viewModel.syncStatus.observe(viewLifecycleOwner) {
            updateSwipeRefreshTripsVisibility(false)
        }

        //viewModel.fetchChallengeList()
        viewModel.syncChallengesError.observe(viewLifecycleOwner) {
            if (!it) {
                Toast.makeText(
                    context,
                    R.string.dk_challenge_failed_to_sync_challenges,
                    Toast.LENGTH_SHORT
                ).show()
            }
            //viewModel.filterChallenges()
        }
        //setViewPager()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateChallenge() {
        if (this::viewModel.isInitialized) {
            //viewModel.fetchChallengeList(SynchronizationType.CACHE)
        }
    }

    /*private fun setViewPager() {
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
    }*/

    /*private fun updateProgressVisibility(displayProgress: Boolean) {
        binding.progressCircular.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }*/

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[ChallengeListViewModel::class.java]
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
    }

    private fun updateDateSelector() {
        if (this.viewModel.dateSelectorViewModel.hasDates()) {
            binding.dateSelectorContainer.visibility = View.VISIBLE
            this.dateSelectorView.configure(viewModel.dateSelectorViewModel)
        } else {
            // TODO should DateSelector remains always visible?
        }
    }

    private fun updateSwipeRefreshTripsVisibility(display: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = display
    }

    private fun configureTabSelectorView() {
        binding.tabLayoutChallenge.apply {
            removeAllTabs()

            ChallengeListCategory.values().forEach {
                val tab = newTab()
                tab.setText(it.getText())
                tab.tag = it
                addTab(tab)
            }

            setSelectedTabIndicatorColor(DriveKitUI.colors.secondaryColor())
            setBackgroundColor(DriveKitUI.colors.backgroundViewColor())

            addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    //TODO trackscreen here ?
                    (tab?.tag as ChallengeListCategory?)?.let {
                        this@ChallengeFragment.viewModel.updateSelectedCategory(it)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun configureDateSelector() {
        this.context?.let {
            this.dateSelectorView = DKDateSelectorView(it)
            this.dateSelectorView.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                configure(this@ChallengeFragment.viewModel.dateSelectorViewModel)
            }
            binding.dateSelectorContainer.apply {
                removeAllViews()
                addView(dateSelectorView)
            }
        }
    }

    private fun updateData() {
        updateSwipeRefreshTripsVisibility(true)
        this.viewModel.updateData()
    }
}
