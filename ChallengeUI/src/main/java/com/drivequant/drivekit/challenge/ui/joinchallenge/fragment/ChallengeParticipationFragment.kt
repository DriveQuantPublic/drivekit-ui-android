package com.drivequant.drivekit.challenge.ui.joinchallenge.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.common.ChallengeHeaderView
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeRulesActivity
import com.drivequant.drivekit.challenge.ui.joinchallenge.common.TitleProgressBar
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationDisplayState
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.*
import kotlinx.android.synthetic.main.dk_fragment_challenge_join.*
import kotlinx.android.synthetic.main.dk_fragment_challenge_join.progress_circular
import kotlin.math.roundToInt


class ChallengeParticipationFragment : Fragment() {

    private lateinit var challengeId: String
    private lateinit var viewModel: ChallengeParticipationViewModel
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var challengeHeaderView:ChallengeHeaderView

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
        context?.let { context ->
            viewModel.syncJoinChallengeError.observe(this, {
                if (it) {
                    if (viewModel.isChallengeStarted()) {
                        progress(context)
                    } else {
                        countDown(context)
                    }
                } else {
                    Toast.makeText(
                        context,
                        DKResource.convertToString(
                            context,
                            "dk_challenge_failed_to_join"
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                updateProgressVisibility(false)
            })

            if (!this::challengeHeaderView.isInitialized) {
                challengeHeaderView = ChallengeHeaderView(context)
            }
        }
        challengeHeaderView.configure(viewModel, requireActivity())
        challenge_header_view_container.addView(challengeHeaderView)

        dispatch()

        viewModel.challenge?.let { challenge ->
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
        context?.let { context ->
            viewModel.manageChallengeDisplayState()?.let {
                when (it) {
                    ChallengeParticipationDisplayState.PROGRESS -> progress(context)
                    ChallengeParticipationDisplayState.JOIN -> join(context)
                    ChallengeParticipationDisplayState.COUNT_DOWN -> countDown(context)
                }
            }
        }
    }

    private fun join(context: Context) {
        text_view_join_challenge.apply {
            text = DKResource.convertToString(context, "dk_challenge_participate_button")
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            visibility = View.VISIBLE
        }
    }

    private fun progress(context: Context) {
        container_conditions_info.visibility = View.VISIBLE
        text_view_join_challenge.apply {
            text =
                DKResource.convertToString(context, "dk_challenge_registered_confirmation")
            setBackgroundColor(DriveKitUI.colors.primaryColor())
            visibility = View.VISIBLE
            isEnabled = false
        }

        viewModel.challenge?.let {
            for (key in it.conditions.keys.reversed()) {
                val progressBar = TitleProgressBar(context)
                val progress = viewModel.computeDriverProgress(it.driverConditions.getValue(key), it.conditions.getValue(key))
                progressBar.apply {
                    setTitle(
                        key,
                        "${it.driverConditions.getValue(key).toDouble()
                            .roundToInt()}/${it.conditions.getValue(key).toDouble().roundToInt()}"
                    )
                    setProgress(progress)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                }
                challenge_layout.addView(progressBar)
            }
        }
    }

    private fun countDown(context: Context) {
        text_view_join_challenge.apply {
            text =
                DKResource.convertToString(context, "dk_challenge_registered_confirmation")
            setBackgroundColor(DriveKitUI.colors.primaryColor())
            isEnabled = false
        }
        if (viewModel.getTimeLeft() > 0) {
            startCountDown(context)
            timer_container.apply {
                setBackgroundColor(DriveKitUI.colors.primaryColor())
                visibility = View.VISIBLE
            }
            challenge_start.apply {
                text = DKResource.convertToString(context, "dk_challenge_start")
                setTextColor(DriveKitUI.colors.fontColorOnPrimaryColor())
            }
        }
    }

    private fun stopCountDown() {
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

    private fun startCountDown(context: Context) {
        val difference = viewModel.getTimeLeft()
        countDownTimer = object : CountDownTimer(difference, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val data =
                    DKDataFormatter.formatExactDuration(context, millisUntilFinished)
                val spannable = DKSpannable()
                data.forEach {
                    when (it) {
                        is FormatType.VALUE -> spannable.append(
                            it.value,
                            context.resSpans {
                                color(DriveKitUI.colors.fontColorOnPrimaryColor())
                                typeface(Typeface.BOLD)
                                size(R.dimen.dk_text_xbig)
                            })
                        is FormatType.UNIT -> spannable.append(
                            it.value,
                            context.resSpans {
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
        progress_circular?.apply {
            visibility = if (displayProgress) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun setStyle() {
        text_view_conditions_info.headLine2(DriveKitUI.colors.fontColorOnPrimaryColor())
        container_conditions_info.setBackgroundColor(DriveKitUI.colors.primaryColor())
        text_view_join_challenge.setBackgroundColor(DriveKitUI.colors.primaryColor())
        text_view_join_challenge.setTextColor(DriveKitUI.colors.fontColorOnPrimaryColor())
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