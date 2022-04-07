package com.drivekit.demoapp.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.core.DriveKit

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        launchTimer {
            if (DriveKit.isUserConnected()) {
                UserInfoActivity::class.java
            } else {
                ApiKeyActivity::class.java
            }.let {
                val intent = Intent(this@SplashScreenActivity, it)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun launchTimer(runnable: Runnable) {
        Handler(Looper.getMainLooper()).postDelayed(runnable, SPLASH_TIME_OUT)
    }
}