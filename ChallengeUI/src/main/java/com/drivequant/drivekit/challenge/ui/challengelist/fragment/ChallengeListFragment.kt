package com.drivequant.drivekit.challenge.ui.challengelist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.activity.ChallengeDetailActivity
import com.drivequant.drivekit.challenge.ui.challengelist.adapter.ChallengeListAdapter
import com.drivequant.drivekit.challenge.ui.challengelist.model.ChallengeListCategory
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeData
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeListViewModel
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeListener
import com.drivequant.drivekit.challenge.ui.databinding.DkFragmentChallengeBinding
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorView
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.updateTabsFont
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class ChallengeListFragment : Fragment(), ChallengeListener {

    private lateinit var viewModel: ChallengeListViewModel
    private var _binding: DkFragmentChallengeBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    private lateinit var dateSelectorView: DKDateSelectorView
    private var adapter: ChallengeListAdapter? = null

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
            updateChallengeList()
        }

        this.viewModel.syncStatus.observe(viewLifecycleOwner) { success ->
            updateSwipeRefreshTripsVisibility(false)
            if (!success) {
                Toast.makeText(context, R.string.dk_challenge_failed_to_sync_challenges, Toast.LENGTH_SHORT).show()
            }
        }

        binding.dkRecyclerViewChallenge.layoutManager = LinearLayoutManager(requireContext())
        updateSwipeRefreshTripsVisibility(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateChallenge() {
        if (this::viewModel.isInitialized) {
            viewModel.updateLocalData()
        }
    }

    private fun updateChallengeList() {
        this.adapter?.update(this.viewModel.currentChallenges) ?: run {
            context?.let {
                this.adapter = ChallengeListAdapter(
                    it,
                    this@ChallengeListFragment.viewModel.currentChallenges,
                    this
                )
            }
        }
        if (binding.dkRecyclerViewChallenge.adapter == null) {
            binding.dkRecyclerViewChallenge.adapter = adapter
        }

        if (this.viewModel.hasChallengesToDisplay) {
            displayChallenges()
        } else {
            displayNoChallenges()
        }
    }

    private fun displayChallenges() {
        binding.noChallenges.viewGroupEmptyScreen.visibility = View.GONE
        binding.dkRecyclerViewChallenge.apply {
            visibility = View.VISIBLE
            scrollToPosition(0)
        }
    }

    private fun displayNoChallenges() {
        binding.noChallenges.apply {
            dkTextViewNoChallenge.apply {
                setText(this@ChallengeListFragment.viewModel.computeNoChallengeTextResId())
                headLine2(DriveKitUI.colors.mainFontColor())
            }
            view?.resources?.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_margin_half)?.let { cornerRadius ->
                this.noChallenges.roundCorners(cornerRadius, cornerRadius, cornerRadius, cornerRadius)
            }
            DrawableCompat.setTint(this.noChallenges.background, DriveKitUI.colors.neutralColor())
            viewGroupEmptyScreen.visibility = View.VISIBLE
        }
        binding.dkRecyclerViewChallenge.visibility = View.GONE
    }

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
        this.dateSelectorView.configure(viewModel.dateSelectorViewModel)
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

            setTabTextColors(
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.secondaryColor()
            )
            setSelectedTabIndicatorColor(DriveKitUI.colors.secondaryColor())
            setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
            updateTabsFont()

            addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    DriveKitUI.analyticsListener?.trackScreen(
                        getString(this@ChallengeListFragment.viewModel.getScreenTagResId()),
                        javaClass.simpleName
                    )

                    (tab?.tag as ChallengeListCategory?)?.let {
                        this@ChallengeListFragment.viewModel.updateSelectedCategory(it)
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
                configure(this@ChallengeListFragment.viewModel.dateSelectorViewModel)
            }
            binding.dateSelectorContainer.apply {
                removeAllViews()
                addView(dateSelectorView)
            }
        }
    }

    private fun updateData() {
        updateSwipeRefreshTripsVisibility(true)
        this.viewModel.synchronizeChallenges()
    }

    override fun onClickChallenge(challengeData: ChallengeData) {
        when {
            challengeData.shouldDisplayExplaining() -> {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(requireContext())
                    .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                    .positiveButton()
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
                titleTextView?.setText(R.string.app_name)
                descriptionTextView?.setText(R.string.dk_challenge_not_a_participant)
                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }
            challengeData.shouldDisplayChallengeDetail() ->
                ChallengeDetailActivity.launchActivity(
                    requireActivity(),
                    challengeData.challengeId)

            else -> ChallengeParticipationActivity.launchActivity(
                requireActivity(),
                challengeData.challengeId
            )
        }
    }
}
