package com.drivequant.drivekit.tripanalysis.crashfeedback.viewmodel

import androidx.lifecycle.ViewModel
import com.drivekit.tripanalysis.ui.R

internal class CrashFeedbackStep2ViewModel : ViewModel() {
    fun getTitleResId() = R.string.dk_crash_detection_feedback_step2_title

    fun getDescriptionResId () = R.string.dk_crash_detection_feedback_step2_description
}