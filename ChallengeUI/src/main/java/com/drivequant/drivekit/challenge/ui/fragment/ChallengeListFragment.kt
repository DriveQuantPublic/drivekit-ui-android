package com.drivequant.drivekit.challenge.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.dk_fragment_challenge_list.*


class ChallengeListFragment : Fragment() {

    private lateinit var challengeAdapter: ChallengeListAdapter
    private lateinit var viewModel: ChallengeListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(ChallengeListViewModel::class.java)
        }
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_challenge-list"
            ), javaClass.simpleName
        )
        setTabLayout()
        updateChallenge()
        var isToastShowed = false
        viewModel.mutableLiveDataChallengesData.observe(this,
            Observer {
                if (viewModel.syncStatus == ChallengesSyncStatus.FAILED_TO_SYNC_CHALLENGES_CACHE_ONLY && !isToastShowed) {
                    Toast.makeText(
                        context,
                        DKResource.convertToString(requireContext(), "dk_challenge_failed_to_sync_challenges"),
                        Toast.LENGTH_SHORT
                    ).show()
                    isToastShowed = true
                }
                if (it.isEmpty()) {
                    displayNoChallenges(viewModel.selectedChallengeStatusData.statusList)
                } else {
                    displayChallenges()
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_challenge_list, container, false)

    private fun setTabLayout() {
        for (challengeStatusData in viewModel.challengesStatusData) {
            val tab = tab_layout_challenge.newTab()
            val text = DKResource.convertToString(requireContext(), challengeStatusData.textId)
            tab.text = text
            tab_layout_challenge.addTab(tab)
        }

        tab_layout_challenge.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.selectedChallengeStatusData =
                    viewModel.challengesStatusData[tab_layout_challenge.selectedTabPosition]
                viewModel.filterChallenges(viewModel.selectedChallengeStatusData.statusList)
            }
        })
    }

    private fun updateChallenge() {
        updateProgressVisibility(true)
        viewModel.fetchChallengeList()
    }

    private fun displayChallenges() {
        no_challenges.visibility = View.GONE
        dk_recycler_view_challenge.visibility = View.VISIBLE
        updateProgressVisibility(false)
    }

    private fun displayNoChallenges(challengeStatusList: List<ChallengeStatus>) {
        var pair = Pair("dk_challenge_no_active_challenge", "dk_challenge_waiting")
        challengeStatusList.map {
            pair = when (it) {
                ChallengeStatus.FINISHED, ChallengeStatus.ARCHIVED -> Pair(
                    "dk_challenge_no_finished_challenge",
                    "dk_challenge_finished"
                )
                ChallengeStatus.PENDING, ChallengeStatus.SCHEDULED -> Pair(
                    "dk_challenge_no_active_challenge",
                    "dk_challenge_waiting"
                )
            }
        }
        val textView = no_challenges.findViewById<TextView>(R.id.dk_text_view_no_challenge)
        val imageView = no_challenges.findViewById<ImageView>(R.id.dk_image_view_no_challenge)
        textView.text = DKResource.convertToString(requireContext(), pair.first)
        DKResource.convertToDrawable(requireContext(), pair.second)?.let {
            imageView.setImageDrawable(it)
        }
        no_challenges.visibility = View.VISIBLE
        dk_recycler_view_challenge.visibility = View.GONE
        updateProgressVisibility(false)
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }
}