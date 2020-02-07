package com.drivekit.demoapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ui.StreaksViewConfig
import com.drivequant.drivekit.driverachievement.ui.streaks.fragment.StreaksListFragment

class StreaksListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaks_list)

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container, StreaksListFragment.newInstance(
                    StreaksViewConfig(applicationContext)
                )
            )
            .commit()
    }
}
