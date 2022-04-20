package com.drivekit.demoapp.onboarding.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drivekit.demoapp.config.DriveKitListenerController
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.core.DriveKit

class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        DriveKitListenerController.reset()
        DriveKit.config.apiKey?.let {
            DriveKit.setApiKey(it)
        }
    }
}