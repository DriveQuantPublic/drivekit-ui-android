package com.drivequant.drivekit.challenge.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.challenge.ChallengesSyncStatus
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.adapter.ChallengeListAdapter
import com.drivequant.drivekit.challenge.ui.viewmodel.ChallengeListViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.dk_fragment_challenge_list.*
import kotlinx.android.synthetic.main.dk_fragment_ranking_component.progress_circular


class ChallengeListFragment : Fragment() {

    private lateinit var challengeAdapter: ChallengeListAdapter
    private lateinit var viewModel: ChallengeListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(ChallengeListViewModel::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_challenge_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_challenge-list"
            ), javaClass.simpleName
        )
        setTabLayout()
    }

    private fun setTabLayout() {
        for (challengeStatusData in viewModel.challengesStatusData) {
            val tab = tab_layout_challenge.newTab()
            val icon = DKResource.convertToDrawable(requireContext(), challengeStatusData.iconId)
            icon?.let {
                tab.setIcon(it)
            }
            tab_layout_challenge.addTab(tab)
        }

        for (i in 0 until tab_layout_challenge.tabCount) {
            val tab = tab_layout_challenge.getTabAt(i)
            tab?.setCustomView(R.layout.dk_challenge_view_tab)
        }

        tab_layout_challenge.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.selectedChallengeStatusData =
                    viewModel.challengesStatusData[tab_layout_challenge.selectedTabPosition]
                updateChallenge()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun updateChallenge() {
        var isToastShowed = false
        viewModel.mutableLiveDataChallengesData.observe(this,
            Observer {
                if (viewModel.syncStatus == ChallengesSyncStatus.FAILED_TO_SYNC_CHALLENGES_CACHE_ONLY) {
                    Toast.makeText(
                        context,
                        DKResource.convertToString(requireContext(),""),
                        Toast.LENGTH_SHORT
                    ).show()
                    isToastShowed = true
                }

                dk_recycler_view_challenge.layoutManager =
                    LinearLayoutManager(requireContext())
                if (this::challengeAdapter.isInitialized) {
                    challengeAdapter.notifyDataSetChanged()
                } else {
                    challengeAdapter =
                        ChallengeListAdapter(
                            requireContext(),
                            viewModel
                        )
                    dk_recycler_view_challenge.adapter = challengeAdapter
                }


                updateProgressVisibility(false)
            })

        updateProgressVisibility(true)
        viewModel.fetchChallengeList()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }
}