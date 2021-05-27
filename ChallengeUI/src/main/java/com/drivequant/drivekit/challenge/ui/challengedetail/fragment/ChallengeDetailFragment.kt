package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.ChallengeUI
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_fragment_challenge_detail.*


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
        setViewPager()
    }

    private fun setViewPager() {
        for (item in ChallengeUI.challengeDetailItems) {
            val tab = tab_layout_challenge_detail.newTab()
            val icon = DKResource.convertToDrawable(requireContext(), item.getImageResource())
            icon?.let {
                tab.setIcon(it)
            }
            tab_layout_challenge_detail.addTab(tab)
        }

        for (i in 0 until tab_layout_challenge_detail.tabCount) {
            val tab = tab_layout_challenge_detail.getTabAt(i)
            tab?.setCustomView(R.layout.dk_challenge_detail_view_tab)
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