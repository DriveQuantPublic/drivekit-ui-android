package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.challenge.ui.databinding.DkFragmentChallengeResultsBinding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable


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
            outState.putString("challengeIdTag", viewModel.challengeId)
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
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_challenge_detail_results), javaClass.simpleName)

        savedInstanceState?.getString("challengeIdTag")?.let {
            viewModel = ViewModelProvider(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
            )[ChallengeDetailViewModel::class.java]
        }

        viewModel.syncChallengeDetailError.observe(viewLifecycleOwner) {
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
            binding.textViewWorst.text = viewModel.getWorstPerformance()
            binding.textViewBest.text = viewModel.getBestPerformance()
            binding.textViewCardScore.text = viewModel.getMainScore(requireContext())
            binding.textViewCardTitle.setText(viewModel.getChallengeResultScoreTitleResId())

            ContextCompat.getDrawable(requireContext(), R.drawable.dk_challenge_first_driver)?.let {
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayCards() {
        val context = requireContext()
        binding.cardViewUserStats.textViewTitle.text = "${viewModel.getDriverDistance(context)} - ${viewModel.getDriverTripsNumber(context)}"
        binding.cardViewUserStats.textViewDescription.setText(R.string.dk_challenge_synthesis_my_stats)

        binding.cardViewDistance.textViewTitle.text = viewModel.getCompetitorDistance(context)
        binding.cardViewDistance.textViewDescription.setText(R.string.dk_challenge_synthesis_total_distance)

        binding.cardViewRanking.textViewTitle.text = DKSpannable()
            .append(context, "${viewModel.getNbDriverRanked()} ", DriveKitUI.colors.primaryColor(), DKStyle.HIGHLIGHT_SMALL)
            .append(context, resources.getQuantityString(R.plurals.dk_challenge_synthesis_ranked, viewModel.getNbDriverRanked()), DriveKitUI.colors.complementaryFontColor(), DKStyle.NORMAL_TEXT)
            .append(context, " / ${viewModel.getNbDriverRegistered()} ", DriveKitUI.colors.primaryColor(), DKStyle.HIGHLIGHT_SMALL)
            .append(context, resources.getQuantityString(R.plurals.dk_challenge_synthesis_registered, viewModel.getNbDriverRegistered()), DriveKitUI.colors.complementaryFontColor(), DKStyle.NORMAL_TEXT)
            .toSpannable()
        binding.cardViewRanking.textViewDescription.text = DKResource.buildString(
            context,
            DriveKitUI.colors.complementaryFontColor(),
            DriveKitUI.colors.primaryColor(),
            R.string.dk_challenge_synthesis_ranked_percentage,
            viewModel.getNbDriverRankedPercentage(),
            textSize = com.drivequant.drivekit.common.ui.R.dimen.dk_text_small,
            highlightSize = com.drivequant.drivekit.common.ui.R.dimen.dk_text_small
        )
    }

    private fun setStyle() {
        binding.textViewCardTitle.setTextColor(DriveKitUI.colors.mainFontColor())
        binding.cardViewUserStats.textViewTitle.highlightSmall(DriveKitUI.colors.primaryColor())
        binding.cardViewDistance.textViewDescription.smallText(DriveKitUI.colors.complementaryFontColor())
        binding.cardViewDistance.textViewTitle.highlightSmall(DriveKitUI.colors.primaryColor())
    }
}
