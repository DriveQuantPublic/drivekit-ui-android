package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.ChallengeUI
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.adapter.ChallengeDetailFragmentPagerAdapter
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_fragment_challenge_detail.*
import kotlinx.android.synthetic.main.dk_fragment_challenge_detail.progress_circular


class ChallengeDetailFragment : Fragment() {

    private lateinit var challengeId: String
    private lateinit var viewModel: ChallengeDetailViewModel

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
        (savedInstanceState?.getString("challengeIdTag"))?.let { it ->
            challengeId = it
        }
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(challengeId)
            ).get(ChallengeDetailViewModel::class.java)
        }
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
            } else {
                setViewPager()
            }
            updateProgressVisibility(false)
        })
        updateChallengeDetail()
    }

    private fun updateChallengeDetail() {
        updateProgressVisibility(true)
        viewModel.fetchChallengeDetail()
    }

    private fun setViewPager() {
        view_pager_challenge_detail.offscreenPageLimit = ChallengeUI.challengeDetailItems.size
        view_pager_challenge_detail.adapter =
            ChallengeDetailFragmentPagerAdapter(
                requireContext(),
                childFragmentManager,
                viewModel
            )
        tab_layout_challenge_detail.setupWithViewPager(view_pager_challenge_detail)
        for ((index, item) in ChallengeUI.challengeDetailItems.withIndex()) {
            tab_layout_challenge_detail?.getTabAt(index)?.let {
                it.icon = ContextCompat.getDrawable(requireContext(), item.getImageResource())
            }
        }
    }

    override fun onDestroy() {
        viewModel.useCache = false
        super.onDestroy()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }
}