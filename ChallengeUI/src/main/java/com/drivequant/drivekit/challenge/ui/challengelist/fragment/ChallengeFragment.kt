package com.drivequant.drivekit.challenge.ui.challengelist.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengelist.adapter.ChallengesFragmentPagerAdapter
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeListViewModel
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import kotlinx.android.synthetic.main.dk_fragment_challenge.*


class ChallengeFragment : Fragment() {

    private lateinit var viewModel: ChallengeListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(ChallengeListViewModel::class.java)
        }
        updateProgressVisibility(true)
        viewModel.fetchChallengeList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.syncChallengesError.observe(this, Observer {
            if(!it){
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
            setViewPager()
            updateProgressVisibility(false)
        })
    }

    fun updateChallenge() {
        viewModel.fetchChallengeList(SynchronizationType.CACHE)
    }

    private fun setViewPager() {
        view_pager_challenge.adapter =
            ChallengesFragmentPagerAdapter(
                childFragmentManager,
                viewModel, requireContext()
            )
        tab_layout_challenge.setupWithViewPager(view_pager_challenge)
        tab_layout_challenge.setBackgroundColor(Color.WHITE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dk_fragment_challenge, container, false)
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }
}