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
import com.drivequant.drivekit.common.ui.extension.format
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

        dk_challenge_progress_bar.progress = viewModel.computeProgressBarPercentage()
        dk_challenge_rating_bar.rating = viewModel.computeRatingStartCount()

        viewModel.challengeDetailData?.let {
            text_view_worst.text = "${it.challengeStats.minScore.format(2)} ${viewModel.getUnit(requireContext())}"
            text_view_best.text = "${it.challengeStats.maxScore.format(2)} ${viewModel.getUnit(requireContext())}"
            text_view_card_score.text = it.driverStats.score.format(2)
            text_view_card_title.text = viewModel.getChallengeResultScoreTitle()
            text_view_unit.text = viewModel.getUnit(requireContext())
        }

        DKResource.convertToDrawable(requireContext(), "dk_challenge_first_driver")?.let {
            if (viewModel.isUserTheFirst()) {
                it.tintDrawable(Color.parseColor("#FFD232"))
            }
            image_view_reword_icon.setImageDrawable(it)
        }

        setStyle()
    }

    private fun setStyle() {
        text_view_card_title.setText(DriveKitUI.colors.mainFontColor())
        text_view_card_score.setText(DriveKitUI.colors.primaryColor())
        text_view_unit.setText(DriveKitUI.colors.mainFontColor())
    }
}