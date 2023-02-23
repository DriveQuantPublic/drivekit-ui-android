package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_challenge_result_card_view.view.*
import kotlinx.android.synthetic.main.dk_fragment_challenge_results.*
import kotlinx.android.synthetic.main.dk_fragment_challenge_results.card_view_distance


class ChallengeResultsFragment : Fragment() {

    private lateinit var viewModel: ChallengeDetailViewModel

    companion object {
        fun newInstance(viewModel: ChallengeDetailViewModel): ChallengeResultsFragment {
            val fragment = ChallengeResultsFragment()
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
        savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dk_fragment_challenge_results, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_challenge_detail_results"
            ), javaClass.simpleName
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.getString("challengeIdTag")?.let {
            viewModel = ViewModelProvider(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
            )[ChallengeDetailViewModel::class.java]
        }

        viewModel.syncChallengeDetailError.observe(this, Observer {
            val colorStateList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dkRatingBarForegroundColor
                    )
                )
            dk_challenge_progress_bar.progressTintList = colorStateList
            dk_challenge_progress_bar.progress = viewModel.getDriverProgress()
            dk_challenge_rating_bar.rating = viewModel.computeRatingStartCount()
            text_view_result_global_rank.text = viewModel.challengeGlobalRank(requireContext())
            text_view_worst.text = viewModel.getWorstPerformance(requireContext())
            text_view_best.text = viewModel.getBestPerformance(requireContext())
            text_view_card_score.text = viewModel.getMainScore(requireContext())
            text_view_card_title.text = DKResource.convertToString(
                requireContext(),
                viewModel.getChallengeResultScoreTitle()
            )

            DKResource.convertToDrawable(requireContext(), "dk_challenge_first_driver")?.let {
                if (viewModel.isUserTheFirst()) {
                    it.tintDrawable(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.dkRatingBarForegroundColor
                        )
                    )
                }
                image_view_reward_icon.setImageDrawable(it)
            }

            if (viewModel.shouldDisplayMinScore()) {
                text_view_worst.visibility = View.INVISIBLE
            }

            displayCards()
            setStyle()
        })
    }


    private fun displayCards() {
        DKResource.convertToDrawable(requireContext(), "dk_common_road")?.let { drawable ->
            card_view_distance.icon_distance.setImageDrawable(drawable)
        }
        viewModel.shouldDisplayDistanceCard().let {
            card_view_distance.visibility = it
            card_view_distance.text_view_driver_value.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_driver_distance",
                viewModel.getDriverDistance(requireContext()))

            card_view_distance.text_view_competitor_value.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_competitors_distance",
                viewModel.getCompetitorDistance(requireContext())
            )
        }

        viewModel.shouldDisplayDurationCard().let {
            DKResource.convertToDrawable(requireContext(), "dk_common_clock")?.let { drawable ->
                card_view_duration.icon_distance.setImageDrawable(drawable)
            }
            card_view_duration.visibility = it
            card_view_duration.text_view_driver_value.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_driver_duration",
                viewModel.getDriverDuration(requireContext()))

            card_view_duration.text_view_competitor_value.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_competitors_duration",
                viewModel.getCompetitorDuration(requireContext())
            )
        }

        viewModel.shouldDisplayTripsCard().let {
             DKResource.convertToDrawable(requireContext(), "dk_common_trip")?.let { drawable ->
                 card_view_trips_number.icon_distance.setImageDrawable(drawable)
            }
            card_view_trips_number.visibility = it
            card_view_trips_number.text_view_driver_value.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_driver_trips",
                viewModel.getDriverTripsNumber(requireContext()))

            card_view_trips_number.text_view_competitor_value.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_competitors_trips",
                viewModel.getCompetitorTripsNumber(requireContext()))
        }
    }

    private fun setStyle() {
        text_view_card_title.setTextColor(DriveKitUI.colors.mainFontColor())
    }
}
