package com.drivequant.drivekit.driverachievement.ui.streaks.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.driverachievement.ui.R

class StreaksListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_streaks_list)
        DriveKitNavigationController.driverAchievementUIEntryPoint?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, it.createStreakListFragment())
                .commit()
        }
    }
}
