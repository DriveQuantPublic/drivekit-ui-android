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
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
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
        inflater.inflate(R.layout.dk_fragment_challenge_results, container, false)

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
            viewModel = ViewModelProviders.of(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
            ).get(ChallengeDetailViewModel::class.java)
        }

        viewModel.syncChallengeDetailError.observe(this) {
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
            text_view_result_global_rank.apply {
                text = viewModel.challengeGlobalRank(requireContext())
                typeface = DriveKitUI.primaryFont(context)
            }
            text_view_worst.apply {
                text = viewModel.getWorstPerformance(requireContext())
                typeface = DriveKitUI.primaryFont(context)
            }
            text_view_best.apply {
                text = viewModel.getBestPerformance(requireContext())
                typeface = DriveKitUI.primaryFont(context)
            }
            text_view_card_score.apply {
                text = viewModel.getMainScore(requireContext())
                typeface = DriveKitUI.primaryFont(context)
            }
            text_view_card_title.apply {
                text = DKResource.convertToString(
                    requireContext(),
                    viewModel.getChallengeResultScoreTitle()
                )
                typeface = DriveKitUI.primaryFont(context)
            }

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
        }
    }


    private fun displayCards() {
        DKResource.convertToDrawable(requireContext(), "dk_common_road")?.let { drawable ->
            card_view_distance.icon_distance.setImageDrawable(drawable)
        }
        viewModel.shouldDisplayDistanceCard().let {
            card_view_distance.apply {
                visibility = it
                text_view_driver_value.apply {
                    text = DKResource.buildString(
                        requireContext(),
                        DriveKitUI.colors.mainFontColor(),
                        DriveKitUI.colors.primaryColor(),
                        "dk_challenge_driver_distance",
                        viewModel.getDriverDistance(requireContext())
                    )
                    typeface = DriveKitUI.primaryFont(context)
                }

                text_view_competitor_value.apply {
                    text = DKResource.buildString(
                        requireContext(),
                        DriveKitUI.colors.complementaryFontColor(),
                        DriveKitUI.colors.primaryColor(),
                        "dk_challenge_competitors_distance",
                        viewModel.getCompetitorDistance(requireContext())
                    )
                    typeface = DriveKitUI.primaryFont(context)
                }
            }
        }

        viewModel.shouldDisplayDurationCard().let {
            DKResource.convertToDrawable(requireContext(), "dk_common_clock")?.let { drawable ->
                card_view_duration.icon_distance.setImageDrawable(drawable)
            }
            card_view_duration.apply {
                visibility = it

                text_view_driver_value.apply {
                    text = DKResource.buildString(
                        requireContext(),
                        DriveKitUI.colors.mainFontColor(),
                        DriveKitUI.colors.primaryColor(),
                        "dk_challenge_driver_duration",
                        viewModel.getDriverDuration(requireContext())
                    )
                    typeface = DriveKitUI.primaryFont(context)
                }

                text_view_competitor_value.apply {
                    text = DKResource.buildString(
                        requireContext(),
                        DriveKitUI.colors.complementaryFontColor(),
                        DriveKitUI.colors.primaryColor(),
                        "dk_challenge_competitors_duration",
                        viewModel.getCompetitorDuration(requireContext())
                    )
                    typeface = DriveKitUI.primaryFont(context)
                }
            }
        }

        viewModel.shouldDisplayTripsCard().let {
            DKResource.convertToDrawable(requireContext(), "dk_common_trip")?.let { drawable ->
                card_view_trips_number.icon_distance.setImageDrawable(drawable)
            }
            card_view_trips_number.apply {
                visibility = it
                text_view_driver_value.apply {
                    text = DKResource.buildString(
                        requireContext(),
                        DriveKitUI.colors.mainFontColor(),
                        DriveKitUI.colors.primaryColor(),
                        "dk_challenge_driver_trips",
                        viewModel.getDriverTripsNumber(requireContext())
                    )
                    typeface = DriveKitUI.primaryFont(context)
                }

                text_view_competitor_value.apply {
                    text = DKResource.buildString(
                        requireContext(),
                        DriveKitUI.colors.complementaryFontColor(),
                        DriveKitUI.colors.primaryColor(),
                        "dk_challenge_competitors_trips",
                        viewModel.getCompetitorTripsNumber(requireContext())
                    )
                    typeface = DriveKitUI.primaryFont(context)
                }
            }
        }
    }

    private fun setStyle() {
        text_view_card_title.apply {
            setTextColor(DriveKitUI.colors.mainFontColor())
            typeface = DriveKitUI.primaryFont(context)
        }
    }
}