package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDriverRanking
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.PseudoChangeListener
import com.drivequant.drivekit.common.ui.component.PseudoCheckListener
import com.drivequant.drivekit.common.ui.component.PseudoUtils
import com.drivequant.drivekit.common.ui.component.ranking.views.DKRankingView
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_fragment_challenge_ranking.container


class ChallengeRankingFragment : Fragment() {

    private lateinit var viewModel: ChallengeDetailViewModel

    companion object {
        fun newInstance(viewModel: ChallengeDetailViewModel):ChallengeRankingFragment {
            val fragment = ChallengeRankingFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putString("challengeIdTag", viewModel.getChallengeId())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_challenge_ranking, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_challenge_detail_ranking"
            ), javaClass.simpleName
        )

        savedInstanceState?.getString("challengeIdTag")?.let {
            if (!this::viewModel.isInitialized) {
                viewModel = ViewModelProvider(
                    this,
                    ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
                )[ChallengeDetailViewModel::class.java]
            }
        }
    }

    override fun onResume() {
        super.onResume()
        PseudoUtils.checkPseudo(object : PseudoCheckListener {
            override fun onPseudoChecked(hasPseudo: Boolean) {
                context?.let {
                if (hasPseudo) {
                    showFragment(it)
                } else {
                        PseudoUtils.show(it, object : PseudoChangeListener {
                            override fun onPseudoChanged(success: Boolean) {
                                if (!success) {
                                    Toast.makeText(it, DKResource.convertToString(it, "dk_common_error_message"), Toast.LENGTH_LONG).show()
                                }
                                fetchDetail()
                            }
                            override fun onCancelled() {
                                showFragment(it)
                            }
                        })
                    }
                }
            }
        })
    }

    private fun fetchDetail() {
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
            context?.let { context ->
                showFragment(context)
            }
        })
        viewModel.fetchChallengeDetail()
    }

    private fun showFragment(context : Context) {
        val ranking = DKRankingView(context)
        ranking.apply {
            this.configure(ChallengeDriverRanking(viewModel))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        if (this.isVisible) {
            container.addView(ranking)
        }
    }
}
