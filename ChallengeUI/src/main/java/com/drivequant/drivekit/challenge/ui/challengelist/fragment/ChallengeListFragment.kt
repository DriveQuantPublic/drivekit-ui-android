package com.drivequant.drivekit.challenge.ui.challengelist.fragment

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
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.activity.ChallengeDetailActivity
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.challenge.ui.challengelist.adapter.ChallengeListAdapter
import com.drivequant.drivekit.challenge.ui.challengelist.viewmodel.*
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus
import kotlinx.android.synthetic.main.dk_fragment_challenge_list.*


class ChallengeListFragment : Fragment(), ChallengeListener {

    private lateinit var viewModel: ChallengeListViewModel
    private lateinit var status: List<ChallengeStatus>
    private var adapter: ChallengeListAdapter? = null


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

        dk_recycler_view_challenge.layoutManager = LinearLayoutManager(requireContext())
        if (viewModel.activeChallenges.isEmpty() || viewModel.finishedChallenges.isEmpty()) {
            displayNoChallenges(status)
        } else {
            adapter?.notifyDataSetChanged() ?: run {
                adapter = ChallengeListAdapter(
                    requireContext(),
                    viewModel,
                    status,
                    this
                )
                dk_recycler_view_challenge.adapter = adapter
            }
            displayChallenges()
        }
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

    override fun onClickChallenge(challengeData: ChallengeData) {
        when {
            challengeData.shouldDisplayExplaining() -> {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(requireContext())
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton()
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                titleTextView?.text = getString(R.string.app_name)
                descriptionTextView?.text =
                    DKResource.convertToString(requireContext(), "dk_challenge_not_a_participant")
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