package com.drivequant.drivekit.tripanalysis.crashfeedback.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.crashfeedback.viewmodel.CrashFeedbackStep1ViewModel
import com.drivequant.drivekit.tripanalysis.crashfeedback.viewmodel.CrashFeedbackStep2ViewModel
import kotlinx.android.synthetic.main.dk_layout_activity_crash_feedback_step1.*
import java.util.*


class CrashFeedbackStep2Activity : BaseCrashFeedbackActivity() {

    private lateinit var viewModel: CrashFeedbackStep2ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                this,
                "dk_tag_trip_analysis_crash_feedback_step2"
            ), javaClass.simpleName
        )

        setContentView(R.layout.dk_layout_activity_crash_feedback_step2)

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(CrashFeedbackStep2ViewModel::class.java)
        }

        /*DriveKitTripAnalysis.setCrashFeedbackTimer(60)
        DriveKitTripAnalysis.setCrashFeedbackListener(object : DKCrashFeedbackListener {
            override fun onProgress(currentSecond: Int, timeoutSecond: Int) {
                runOnUiThread {
                    text_view_progress.text = "$currentSecond"
                    text_view_total.text = "$timeoutSecond"
                }
            }

            override fun timeoutReached() {
                runOnUiThread {
                    Toast.makeText(DriveKit.applicationContext!!, "Timeout reached !", Toast.LENGTH_SHORT).show()
                    sendNoCrash()
                }
            }
        })*/

        initTitle()
        initDescription()

        // TODO setup 60" timer
    }

    private fun initTitle() {
        viewModel.getTitleResId().let {
            text_view_title.text = this.getText(it)
            text_view_title.highlightMedium()
        }
    }

    private fun initDescription() {
        viewModel.getDescriptionResId().let {
            text_view_description.text = this.getText(it)
            text_view_description.highlightMedium()
        }
    }

    /*fun onNoCrashButtonClicked(view: View) {
        sendNoCrash()
    }

    fun onMinorCrashButtonClicked(view: View) {
        DriveKitTripAnalysis.sendUserFeedback(
            feedbackType = CrashFeedbackType.CRASH_CONFIRMED,
            severity = CrashFeedbackSeverity.MINOR,
            listener = object : CrashUserFeedbackListener {
                override fun onFeedbackSent(status: CrashFeedbackStatus) {
                    view.context?.let {
                        Toast.makeText(it, "Minor crash feedback status: $status", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        )
    }

    fun onCriticalCrashButtonClicked(view: View) {
        dismissKeyguard()
        launchPhoneCall() // Do not wait the webservice response to make the phone call !
        DriveKitTripAnalysis.sendUserFeedback(
            feedbackType = CrashFeedbackType.CRASH_CONFIRMED,
            severity = CrashFeedbackSeverity.CRITICAL,
            listener = object : CrashUserFeedbackListener {
                override fun onFeedbackSent(status: CrashFeedbackStatus) {
                    view.context?.let {
                        Toast.makeText(it, "Critical crash feedback status: $status", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(DriveKit.applicationContext!!, "No crash feedback status: $status", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        )
    }*/

    override fun onDestroy() {
        super.onDestroy()
        //DriveKitTripAnalysis.setCrashFeedbackListener(null)
    }
}