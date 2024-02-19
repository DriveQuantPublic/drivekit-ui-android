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
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.common.ChallengeHeaderView
import com.drivequant.drivekit.challenge.ui.databinding.DkFragmentChallengeJoinBinding
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeRulesActivity
import com.drivequant.drivekit.challenge.ui.joinchallenge.common.TitleProgressBar
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationDisplayState
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.FormatType
import kotlin.math.roundToInt

class ChallengeParticipationFragment : Fragment() {

    private lateinit var challengeId: String
    private lateinit var viewModel: ChallengeParticipationViewModel
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var challengeHeaderView: ChallengeHeaderView
    private var _binding: DkFragmentChallengeJoinBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

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
    ): View {
        _binding = DkFragmentChallengeJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle()
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_challenge_join), javaClass.simpleName)

        savedInstanceState?.getString("challengeIdTag")?.let {
            challengeId = it
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(
                this,
                ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(challengeId)
            )[ChallengeParticipationViewModel::class.java]
        }

        viewModel.syncJoinChallengeError.observe(viewLifecycleOwner) {
            context?.let { context ->
                if (it) {
                    if (viewModel.isChallengeStarted()) {
                        progress(context)
                    } else {
                        countDown(context)
                    }
                } else {
                    Toast.makeText(
                        context,
                        R.string.dk_challenge_failed_to_join,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                updateProgressVisibility(false)
            }
        }

        if (!this::challengeHeaderView.isInitialized && context != null) {
            challengeHeaderView = ChallengeHeaderView(requireContext())
        }
        challengeHeaderView.configure(viewModel, requireActivity())
        binding.challengeHeaderViewContainer.addView(challengeHeaderView)

        dispatch()

        viewModel.challenge?.let { challenge ->
            binding.textViewJoinChallenge.setOnClickListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun dispatch() {
        context?.let { context ->
            viewModel.manageChallengeDisplayState()?.let {
                when (it) {
                    ChallengeParticipationDisplayState.PROGRESS -> progress(context)
                    ChallengeParticipationDisplayState.JOIN -> join()
                    ChallengeParticipationDisplayState.COUNT_DOWN -> countDown(context)
                }
            }
        }
    }

    private fun join() {
        binding.textViewJoinChallenge.visibility = View.VISIBLE
    }

    private fun progress(context: Context) {
        binding.containerConditionsInfo.visibility = View.VISIBLE
        binding.textViewJoinChallenge.visibility = View.GONE

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
                binding.challengeLayout.addView(progressBar)
            }
        }
    }

    private fun countDown(context: Context) {
        binding.textViewJoinChallenge.visibility = View.GONE

        if (viewModel.getTimeLeft() > 0) {
            startCountDown(context)
            binding.timerContainer.apply {
                setBackgroundColor(DriveKitUI.colors.primaryColor())
                visibility = View.VISIBLE
            }
            binding.challengeStart.apply {
                setText(R.string.dk_challenge_start)
                setTextColor(DriveKitUI.colors.fontColorOnPrimaryColor())
                typeface = DriveKitUI.primaryFont(context)
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
                                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xbig)
                            })
                        is FormatType.UNIT -> spannable.append(
                            it.value,
                            context.resSpans {
                                color(DriveKitUI.colors.fontColorOnPrimaryColor())
                                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_normal)
                            })
                        is FormatType.SEPARATOR -> spannable.append(it.value)
                    }
                }
                binding.textViewCountdown.text = spannable.toSpannable()
            }

            override fun onFinish() {}
        }.start()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        binding.progressCircular.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun setStyle() {
        binding.challengeJoined.headLine1(DriveKitUI.colors.fontColorOnPrimaryColor())
        binding.textViewConditionsInfo.headLine2(DriveKitUI.colors.fontColorOnPrimaryColor())
        binding.containerConditionsInfo.setBackgroundColor(DriveKitUI.colors.primaryColor())
        binding.textViewCountdown.normalText(DriveKitUI.colors.fontColorOnPrimaryColor())
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
