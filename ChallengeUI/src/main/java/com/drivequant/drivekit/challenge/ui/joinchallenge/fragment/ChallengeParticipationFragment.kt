package com.drivequant.drivekit.challenge.ui.joinchallenge.fragment

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeRulesActivity
import com.drivequant.drivekit.challenge.ui.joinchallenge.common.TitleProgressBar
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationDisplayState
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.*
import kotlinx.android.synthetic.main.dk_fragment_challenge_join.*
import kotlin.math.roundToInt


class ChallengeParticipationFragment : Fragment() {

    private lateinit var challengeId: String
    private lateinit var viewModel: ChallengeParticipationViewModel
    private lateinit var countDownTimer: CountDownTimer

    companion object {
        fun newInstance(challengeId: String): ChallengeParticipationFragment {
            val fragment = ChallengeParticipationFragment()
            fragment.challengeId = challengeId
            return fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::challengeId.isInitialized) {
            outState.putString("challengeIdTag", challengeId)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.dk_fragment_challenge_join, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setStyle()

        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_challenge_join"
            ), javaClass.simpleName
        )

        (savedInstanceState?.getString("challengeIdTag"))?.let { it ->
            challengeId = it
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(
                this,
                ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(challengeId)
            ).get(ChallengeParticipationViewModel::class.java)
        }

        dispatch()

        viewModel.syncJoinChallengeError.observe(this, Observer {
            if (it) {
                if (viewModel.isChallengeStarted()) {
                    progress()
                } else {
                    countDown()
                }
            } else {
                Toast.makeText(
                    context,
                    DKResource.convertToString(
                        requireContext(),
                        "dk_challenge_failed_to_join"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
            updateProgressVisibility(false)
        })

        viewModel.challenge?.let { challenge ->
            challenge.rules?.let {
                if (it.isNotEmpty()) {
                    text_view_challenge_rule_consult.apply {
                        text = DKResource.convertToString(
                            requireContext(),
                            "dk_challenge_consult_rule_button"
                        )
                        setTextColor(DriveKitUI.colors.secondaryColor())
                        visibility = View.VISIBLE
                        setOnClickListener { _ ->
                            ChallengeRulesActivity.launchActivity(
                                requireActivity(),
                                challenge.challengeId,
                                viewModel.isDriverRegistered()
                            )
                        }
                    }
                }
            }

            challenge.conditionsDescription?.let { conditionsDescription ->
                if (conditionsDescription.isNotEmpty()) {
                    text_view_conditions.text = conditionsDescription
                    text_view_conditions.visibility = View.VISIBLE
                }
            }

            text_view_rules.text = challenge.description
            text_view_date.text = viewModel.getDateRange()
            text_view_title.text = challenge.title
            text_view_join_challenge.setOnClickListener {
                challenge.rules?.let { rules ->
                    if (rules.isNotEmpty()) {
                        ChallengeRulesActivity.launchActivity(
                            requireActivity(),
                            challengeId,
                            viewModel.isDriverRegistered()
                        )
                    }
                } ?: run {
                    updateProgressVisibility(true)
                    viewModel.joinChallenge(challengeId)
                }
            }
        }
    }

    fun dispatch() {
        viewModel.manageChallengeDisplayState()?.let {
            when (it) {
                ChallengeParticipationDisplayState.PROGRESS -> progress()
                ChallengeParticipationDisplayState.JOIN -> join()
                ChallengeParticipationDisplayState.COUNT_DOWN -> countDown()
            }
        }
    }

    private fun join() {
        text_view_join_challenge.apply {
            text = DKResource.convertToString(requireContext(), "dk_challenge_participate_button")
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            visibility = View.VISIBLE
        }
    }

    private fun progress() {
        container_conditions_info.visibility = View.VISIBLE
        view_separator_1.visibility = View.INVISIBLE
        text_view_join_challenge.apply {
            text =
                DKResource.convertToString(requireContext(), "dk_challenge_registered_confirmation")
            visibility = View.VISIBLE
            isEnabled = false
        }

        viewModel.challenge?.let {
            for (key in it.conditions.keys.reversed()) {
                val progressBar = TitleProgressBar(requireContext())
                val progress = it.driverConditions.getValue(key).toDouble()
                    .div(it.conditions.getValue(key).toDouble()) * 100
                progressBar.apply {
                    setTitle(
                        key,
                        "${it.driverConditions.getValue(key).toDouble()
                            .roundToInt()}/${it.conditions.getValue(key).toDouble().roundToInt()}"
                    )
                    setProgress(progress.toInt())
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                }
                challenge_layout.addView(progressBar)
            }
        }
    }

    private fun countDown() {
        text_view_join_challenge.apply {
            text =
                DKResource.convertToString(requireContext(), "dk_challenge_registered_confirmation")
            setBackgroundColor(DriveKitUI.colors.primaryColor())
            isEnabled = false
        }
        if (viewModel.getTimeLeft() > 0) {
            startCountDown()
            timer_container.apply {
                setBackgroundColor(DriveKitUI.colors.primaryColor())
                visibility = View.VISIBLE
            }
            challenge_start.apply {
                text = DKResource.convertToString(requireContext(), "dk_challenge_start")
                setTextColor(DriveKitUI.colors.fontColorOnPrimaryColor())
            }
        }
    }

    private fun stopCountDown() {
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

    private fun startCountDown() {
        val difference = viewModel.getTimeLeft()
        countDownTimer = object : CountDownTimer(difference, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val data =
                    DKDataFormatter.formatExactDuration(requireContext(), millisUntilFinished)
                val spannable = DKSpannable()
                data.forEach {
                    when (it) {
                        is FormatType.VALUE -> spannable.append(
                            it.value,
                            requireContext().resSpans {
                                color(DriveKitUI.colors.fontColorOnPrimaryColor())
                                typeface(Typeface.BOLD)
                                size(R.dimen.dk_text_big)
                            })
                        is FormatType.UNIT -> spannable.append(
                            it.value,
                            requireContext().resSpans {
                                color(DriveKitUI.colors.fontColorOnPrimaryColor())
                                size(R.dimen.dk_text_normal)
                            })
                        is FormatType.SEPARATOR -> spannable.append(it.value)
                    }
                }
                text_view_countdown.text = spannable.toSpannable()
            }

            override fun onFinish() {}
        }.start()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }

    private fun setStyle() {
        text_view_title.setTextColor(DriveKitUI.colors.mainFontColor())
        text_view_date.smallText(Color.parseColor("#9E9E9E"))
        view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        text_view_conditions.normalText()
        text_view_rules.normalText()
        view_separator_1.setBackgroundColor(DriveKitUI.colors.neutralColor())
        text_view_conditions_info.headLine2(DriveKitUI.colors.fontColorOnPrimaryColor())
        text_view_challenge_rule_consult.normalText(DriveKitUI.colors.complementaryFontColor())
        container_conditions_info.setBackgroundColor(DriveKitUI.colors.primaryColor())
        text_view_join_challenge.setBackgroundColor(DriveKitUI.colors.primaryColor())
        text_view_countdown.normalText(DriveKitUI.colors.fontColorOnPrimaryColor())
    }

    override fun onPause() {
        super.onPause()
        stopCountDown()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCountDown()
    }
}