package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_fragment_challenge_results.*


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
        inflater.inflate(R.layout.dk_fragment_challenge_results, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.getString("challengeIdTag")?.let {
            if (!this::viewModel.isInitialized) {
                viewModel = ViewModelProviders.of(
                    this,
                    ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
                ).get(ChallengeDetailViewModel::class.java)
            }
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            val drawable = dk_challenge_progress_bar.progressDrawable
            drawable.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.dkRatingBarForegroundColor),
                PorterDuff.Mode.SRC_IN
            )
        } else {
            val colorStateList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dkRatingBarForegroundColor
                    )
                )
            dk_challenge_progress_bar.progressTintList = colorStateList
        }

        dk_challenge_progress_bar.progress = viewModel.getDriverProgress()
        dk_challenge_rating_bar.rating = viewModel.computeRatingStartCount()
        text_view_result_global_rank.text = viewModel.getDriverGlobalRank(requireContext())
        text_view_worst.text = viewModel.getWorstPerformance(requireContext())
        text_view_best.text = viewModel.getBestPerformance(requireContext())
        text_view_card_score.text = viewModel.getMainScore(requireContext())
        text_view_card_title.text = viewModel.getChallengeResultScoreTitle(requireContext())

        DKResource.convertToDrawable(requireContext(), "dk_challenge_first_driver")?.let {
            if (viewModel.isUserTheFirst()) {
                it.tintDrawable(Color.parseColor("#FFD232"))
            }
            image_view_reword_icon.setImageDrawable(it)
        }

        if (viewModel.shouldDisplayMinScore()) {
            text_view_worst.visibility = View.INVISIBLE
        }

        displayCards()
        setStyle()
    }


    private fun displayCards() {
        viewModel.shouldDisplayDistanceCard().let {
            card_view_distance.visibility = it
            distance_user.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_driver_distance",
                viewModel.getDriverDistance(requireContext()))

            distance_competitor.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_competitors_distance",
                viewModel.getCompetitorDistance(requireContext())
            )
        }

        viewModel.shouldDisplayDurationCard().let {
            card_view_duration.visibility = it
            duration_user.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_driver_duration",
                viewModel.getDriverDuration(requireContext()))

            duration_competitor.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_competitors_duration",
                viewModel.getCompetitorDuration(requireContext())
            )
        }

        viewModel.shouldDisplayTripsCard().let {
            card_view_trip.visibility = it
            trip_user.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_driver_trips",
                viewModel.getDriverTripsNumber(requireContext()))

            trip_competitor.text = DKResource.buildString(
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