package com.drivekit.demoapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController

class StreaksListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaks_list)
        DriveKitNavigationController.driverAchievementUIEntryPoint?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, it.createStreakFragment())
                .commit()
        }
    }
}
