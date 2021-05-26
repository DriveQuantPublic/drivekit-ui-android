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
import kotlinx.android.synthetic.main.dk_fragment_challenge.*


class ChallengeFragment : Fragment() {

    private lateinit var viewModel: ChallengeListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(ChallengeListViewModel::class.java)
        }
        updateChallenge()
        viewModel.mutableLiveDataChallengesData.observe(this,
            Observer {
                viewModel.filterChallenges()
                setViewPager()
                updateProgressVisibility(false)
            })

        viewModel.syncChallengesError.observe(this, Observer {
            it?.let {
                Toast.makeText(
                    context,
                    DKResource.convertToString(
                        requireContext(),
                        "dk_challenge_failed_to_sync_challenges"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
            updateProgressVisibility(false)
        })
    }

    private fun updateChallenge() {
        updateProgressVisibility(true)
        viewModel.fetchChallengeList()
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