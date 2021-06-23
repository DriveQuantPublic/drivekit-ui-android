package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.ChallengeUI
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.adapter.ChallengeDetailFragmentPagerAdapter
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import kotlinx.android.synthetic.main.dk_fragment_challenge_detail.*
import kotlinx.android.synthetic.main.dk_fragment_challenge_detail.progress_circular


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
            viewModel = ViewModelProviders.of(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(challengeId)
            ).get(ChallengeDetailViewModel::class.java)
        }
        startSyncType = if (viewModel.getLocalChallengeDetail() != null) SynchronizationType.CACHE else SynchronizationType.DEFAULT
        viewModel.syncChallengeDetailError.observe(this, Observer {
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
        })
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
            tab_layout_challenge_detail.getTabAt(index)?.let {
                val icon = ImageView(requireContext())
                ContextCompat.getDrawable(requireContext(), item.getImageResource())
                    ?.let { drawable ->
                        icon.setImageDrawable(drawable)
                    }
                it.parent?.let { _ ->
                    it.customView = icon
                }
            }
        }
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }
}