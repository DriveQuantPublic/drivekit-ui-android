package com.drivequant.drivekit.tripanalysis.crashfeedback.viewmodel

import androidx.lifecycle.ViewModel
import com.drivekit.tripanalysis.ui.R

// TODO pass
internal class CrashFeedbackStep1ViewModel : ViewModel() {
    fun getTitleResId() = R.string.dk_crash_detection_feedback_step1_title

    fun getDescriptionResId () = R.string.dk_crash_detection_feedback_step1_description

}