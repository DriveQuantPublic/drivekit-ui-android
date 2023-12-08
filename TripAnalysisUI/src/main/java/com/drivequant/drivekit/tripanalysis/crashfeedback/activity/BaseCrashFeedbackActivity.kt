package com.drivequant.drivekit.tripanalysis.crashfeedback.activity

import android.app.KeyguardManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI

open class BaseCrashFeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    fun dismissKeyguard() {
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager?.requestDismissKeyguard(this, null)
        }
    }

    fun launchPhoneCall() {
        val intentTel = Intent(Intent.ACTION_VIEW)
        val dataTel = Uri.parse("tel:" + DriveKitTripAnalysisUI.crashFeedbackRoadsideAssistanceNumber)
        intentTel.data = dataTel
        try {
            startActivity(intentTel)
        } catch (activityNotFoundException: ActivityNotFoundException) {
            DriveKitLog.e(DriveKitTripAnalysisUI.TAG, "Crash Feedback - Could not launch phone call : $activityNotFoundException")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        // do nothing
    }
}
