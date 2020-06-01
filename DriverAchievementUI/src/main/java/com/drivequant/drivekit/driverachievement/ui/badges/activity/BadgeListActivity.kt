package com.drivequant.drivekit.driverachievement.ui.badges.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.fragment.BadgesListFragment

class BadgeListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_badges_list)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, BadgesListFragment())
            .commit()
    }
}
