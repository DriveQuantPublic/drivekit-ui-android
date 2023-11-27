package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.content.res.ColorStateList
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
import com.drivequant.drivekit.challenge.ui.databinding.DkFragmentChallengeResultsBinding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKResource


class ChallengeResultsFragment : Fragment() {

    private lateinit var viewModel: ChallengeDetailViewModel
    private var _binding: DkFragmentChallengeResultsBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

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
        savedInstanceState: Bundle?): View {
        _binding = DkFragmentChallengeResultsBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_challenge_detail_results"
            ), javaClass.simpleName
        )

        savedInstanceState?.getString("challengeIdTag")?.let {
            viewModel = ViewModelProvider(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
            )[ChallengeDetailViewModel::class.java]
        }

        viewModel.syncChallengeDetailError.observe(viewLifecycleOwner, Observer {
            val colorStateList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dkRatingBarForegroundColor
                    )
                )
            binding.dkChallengeProgressBar.progressTintList = colorStateList
            binding.dkChallengeProgressBar.progress = viewModel.getDriverProgress()
            binding.dkChallengeRatingBar.rating = viewModel.computeRatingStartCount()
            binding.textViewResultGlobalRank.text = viewModel.challengeGlobalRank(requireContext())
            binding.textViewWorst.text = viewModel.getWorstPerformance(requireContext())
            binding.textViewBest.text = viewModel.getBestPerformance(requireContext())
            binding.textViewCardScore.text = viewModel.getMainScore(requireContext())
            binding.textViewCardTitle.text = DKResource.convertToString(
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
                binding.imageViewRewardIcon.setImageDrawable(it)
            }

            if (viewModel.shouldDisplayMinScore()) {
                binding.textViewWorst.visibility = View.INVISIBLE
            }

            displayCards()
            setStyle()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayCards() {
        DKResource.convertToDrawable(requireContext(), "dk_common_road")?.let { drawable ->
            binding.cardViewDistance.iconDistance.setImageDrawable(drawable)
        }
        viewModel.shouldDisplayDistanceCard().let {
            binding.cardViewDistance.cardViewDistance.visibility = it
            binding.cardViewDistance.textViewDriverValue.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_driver_distance",
                viewModel.getDriverDistance(requireContext()))

            binding.cardViewDistance.textViewCompetitorValue.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_competitors_distance",
                viewModel.getCompetitorDistance(requireContext())
            )
        }

        viewModel.shouldDisplayDurationCard().let {
            DKResource.convertToDrawable(requireContext(), "dk_common_clock")?.let { drawable ->
                binding.cardViewDuration.iconDistance.setImageDrawable(drawable)
            }
            binding.cardViewDuration.cardViewDistance.visibility = it
            binding.cardViewDuration.textViewDriverValue.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_driver_duration",
                viewModel.getDriverDuration(requireContext()))

            binding.cardViewDuration.textViewCompetitorValue.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_competitors_duration",
                viewModel.getCompetitorDuration(requireContext())
            )
        }

        viewModel.shouldDisplayTripsCard().let {
             DKResource.convertToDrawable(requireContext(), "dk_common_trip")?.let { drawable ->
                 binding.cardViewTripsNumber.iconDistance.setImageDrawable(drawable)
            }
            binding.cardViewTripsNumber.cardViewDistance.visibility = it
            binding.cardViewTripsNumber.textViewDriverValue.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_driver_trips",
                viewModel.getDriverTripsNumber(requireContext()))

            binding.cardViewTripsNumber.textViewCompetitorValue.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.primaryColor(),
                "dk_challenge_competitors_trips",
                viewModel.getCompetitorTripsNumber(requireContext()))
        }
    }

    private fun setStyle() {
        binding.textViewCardTitle.setTextColor(DriveKitUI.colors.mainFontColor())
    }
}
