package com.drivequant.drivekit.tripanalysis.crashfeedback.activity

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.drivekit.tripanalysis.ui.R
import com.drivekit.tripanalysis.ui.databinding.DkLayoutActivityCrashFeedbackStep2Binding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.pixelToSp
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.crashfeedback.viewmodel.CrashFeedbackStep2ViewModel
import com.drivequant.drivekit.tripanalysis.model.crashdetection.CrashFeedbackStatus
import com.drivequant.drivekit.tripanalysis.model.crashdetection.CrashUserFeedbackListener
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackSeverity
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackType
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.DKCrashFeedbackListener

class CrashFeedbackStep2Activity : BaseCrashFeedbackActivity() {

    private lateinit var viewModel: CrashFeedbackStep2ViewModel
    private lateinit var binding: DkLayoutActivityCrashFeedbackStep2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DkLayoutActivityCrashFeedbackStep2Binding.inflate(layoutInflater)

        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_trip_analysis_crash_feedback_step2), javaClass.simpleName)

        setContentView(binding.root)

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[CrashFeedbackStep2ViewModel::class.java]
        }

        DriveKitTripAnalysis.setCrashFeedbackTimer(60 * 5) // 5 minutes
        DriveKitTripAnalysis.setCrashFeedbackListener(object : DKCrashFeedbackListener {
            override fun onProgress(currentSecond: Int, timeoutSecond: Int) {
                // do nothing
            }

            override fun timeoutReached() {
                sendNoCrash()
            }
        })

        initTitle()
        initDescription()
    }

    private fun initTitle() {
        viewModel.getTitleResId().let { titleResId ->
            binding.textViewTitle.apply {
                setText(titleResId)
                pixelToSp(context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xbigger))
                setTypeface(DriveKitUI.primaryFont(context), Typeface.NORMAL)
            }
        }
    }

    private fun initDescription() {
        viewModel.getDescriptionResId().let { descriptionResId ->
            binding.textViewDescription.apply {
                setText(descriptionResId)
                pixelToSp(context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xbigger))
                setTypeface(DriveKitUI.primaryFont(context), Typeface.NORMAL)
            }
        }
    }

    fun onNoCrashButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        sendNoCrash()
    }

    fun onMinorCrashButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        DriveKitTripAnalysis.sendUserFeedback(
            feedbackType = CrashFeedbackType.CRASH_CONFIRMED,
            severity = CrashFeedbackSeverity.MINOR,
            listener = object : CrashUserFeedbackListener {
                override fun onFeedbackSent(status: CrashFeedbackStatus) {
                    finish()
                }
            }
        )
    }

    fun onCriticalCrashButtonClicked(view: View) {
        launchPhoneCall() // Do not wait the webservice response to make the phone call !
        dismissKeyguard()
        DriveKitTripAnalysis.sendUserFeedback(
            feedbackType = CrashFeedbackType.CRASH_CONFIRMED,
            severity = CrashFeedbackSeverity.CRITICAL,
            listener = object : CrashUserFeedbackListener {
                override fun onFeedbackSent(status: CrashFeedbackStatus) {
                    view.context?.let {
                        finish()
                    }
                }
            }
        )
    }

    private fun sendNoCrash() {
        DriveKitTripAnalysis.sendUserFeedback(
            feedbackType = CrashFeedbackType.NO_CRASH,
            severity = CrashFeedbackSeverity.NONE,
            listener = object : CrashUserFeedbackListener {
                override fun onFeedbackSent(status: CrashFeedbackStatus) {
                    finish()
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        DriveKitTripAnalysis.setCrashFeedbackListener(null)
    }
}
