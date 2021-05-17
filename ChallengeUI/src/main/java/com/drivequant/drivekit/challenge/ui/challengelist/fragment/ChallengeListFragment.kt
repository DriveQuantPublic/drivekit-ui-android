package com.drivequant.drivekit.challenge.ui.challengelist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengelist.adapter.ChallengeListAdapter
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.ChallengeListViewModel
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.containsActiveChallenge
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.toStatusList
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.toStringArray
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus
import kotlinx.android.synthetic.main.dk_fragment_challenge_list.*


class ChallengeListFragment : Fragment() {

    private lateinit var viewModel: ChallengeListViewModel
    private lateinit var status: List<ChallengeStatus>

    companion object {
        fun newInstance(status: List<ChallengeStatus>, viewModel: ChallengeListViewModel): ChallengeListFragment {
            val fragment = ChallengeListFragment()
            fragment.status = status
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dk_fragment_challenge_list, container, false)
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray("status",status.toStringArray())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.getStringArray("status")?.let {
            status = it.toStatusList()
        }

        val tag = if (status.containsActiveChallenge()) {
            "dk_tag_challenge_list_active"
        } else {
            "dk_tag_challenge_list_finished"
        }

        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                tag
            ), javaClass.simpleName
        )

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(ChallengeListViewModel::class.java)
        }

        if (viewModel.activeChallenges.isEmpty() || viewModel.finishedChallenges.isEmpty()) {
            displayNoChallenges(status)
        } else {
            displayChallenges()
        }

        dk_recycler_view_challenge.layoutManager =
            LinearLayoutManager(requireContext())
        dk_recycler_view_challenge.adapter = ChallengeListAdapter(
            requireContext(),
            viewModel,
            status
        )
    }

    private fun displayChallenges() {
        no_challenges.visibility = View.GONE
        dk_recycler_view_challenge.visibility = View.VISIBLE
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
        textView.headLine2(DriveKitUI.colors.mainFontColor())
        no_challenges.visibility = View.VISIBLE
        dk_recycler_view_challenge.visibility = View.GONE
    }
}