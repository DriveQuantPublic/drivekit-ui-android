package com.drivequant.drivekit.challenge.ui.joinchallenge.fragment

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
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
            outState.putString("challengeId", challengeId)
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

        (savedInstanceState?.getString("challengeId"))?.let { it ->
            challengeId = it
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(
                this,
                ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(challengeId)
            ).get(ChallengeParticipationViewModel::class.java)
        }

        viewModel.syncJoinChallengeError.observe(this, Observer {
            if (it) {
                if (viewModel.isChallengeStarted()) {
                    progress()
                } else {
                    countDown()
                }
            }
            updateProgressVisibility(false)
        })

        viewModel.manageChallengeDisplayState()?.let {
            when (it) {
                ChallengeParticipationDisplayState.PROGRESS -> {
                    progress()
                }
                ChallengeParticipationDisplayState.JOIN -> {
                    join()
                }
                ChallengeParticipationDisplayState.COUNT_DOWN -> {
                    countDown()
                }
            }
        }

        viewModel.challenge?.let {
            it.rules?.let { rules ->
                if (rules.isNotEmpty()) {
                    text_view_challenge_rule_consult.text = rules
                    text_view_challenge_rule_consult.visibility = View.VISIBLE
                    text_view_challenge_rule_consult.paintFlags =
                        text_view_challenge_rule_consult.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                }
            }
            it.conditionsDescription?.let { conditionsDescription ->
                if (conditionsDescription.isNotEmpty()) {
                    text_view_conditions.text = conditionsDescription
                    text_view_conditions.visibility = View.VISIBLE
                }
            }
            text_view_rules.text = it.description
            text_view_date.text =
                "${it.startDate.formatDate(DKDatePattern.STANDARD_DATE)} - ${it.endDate.formatDate(
                    DKDatePattern.STANDARD_DATE
                )}"
            text_view_title.text = it.title
        }

        text_view_join_challenge.setOnClickListener {
            updateProgressVisibility(true)
            viewModel.joinChallenge(challengeId)
        }
    }

    private fun join() {
        text_view_join_challenge.apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            text = DKResource.convertToString(requireContext(), "dk_challenge_participate_button")
            visibility = View.VISIBLE
        }
    }

    private fun progress() {
        container_conditions_info.visibility = View.VISIBLE
        text_view_join_challenge.apply {
            text =
                DKResource.convertToString(requireContext(), "dk_challenge_registered_confirmation")
            visibility = View.VISIBLE
            isClickable = false
        }

        viewModel.challenge?.let {
            for (key in it.conditions.keys) {
                val progressBar = TitleProgressBar(requireContext())
                val progress = it.driverConditions.getValue(key).toDouble()
                    .div(it.conditions.getValue(key).toDouble()) * 100
                progressBar.setTitle(
                    key,
                    "${it.driverConditions.getValue(key).toDouble()
                        .roundToInt()} / ${it.conditions.getValue(key).toDouble().roundToInt()}"
                )
                progressBar.setProgress(progress.toInt())

                progressBar.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                challenge_layout.addView(progressBar)
            }
        }
    }

    private fun countDown() {
        if (viewModel.getTimeLeft() > 0) {
            timer_container.visibility = View.VISIBLE
            timer_container.setBackgroundColor(DriveKitUI.colors.primaryColor())
            startCountDown()
            challenge_start.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.fontColorOnPrimaryColor(),
                DriveKitUI.colors.fontColorOnPrimaryColor(),
                "dk_challenge_start",
                viewModel.challenge?.title ?: ""
            )
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
                        is FormatType.UNIT -> spannable.append(it.value, requireContext().resSpans {
                            color(DriveKitUI.colors.fontColorOnPrimaryColor())
                            size(R.dimen.dk_text_normal)
                        })
                        is FormatType.SEPARATOR -> spannable.append(it.value)
                    }
                }

                text_view_countdown.text = spannable.toSpannable()
            }

            override fun onFinish() {
                //TODO What to do next ?
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCountDown()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }

    private fun setStyle() {
        text_view_join_challenge.setBackgroundColor(DriveKitUI.colors.primaryColor())
        text_view_title.setTextColor(DriveKitUI.colors.mainFontColor())
        text_view_conditions_info.headLine2(DriveKitUI.colors.fontColorOnPrimaryColor())
        text_view_rules.normalText()
        text_view_conditions.normalText()
        text_view_challenge_rule_consult.normalText(DriveKitUI.colors.complementaryFontColor())
        text_view_date.smallText()
        text_view_countdown.normalText(DriveKitUI.colors.fontColorOnPrimaryColor())
        container_conditions_info.setBackgroundColor(DriveKitUI.colors.primaryColor())
        view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        view_separator_1.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}