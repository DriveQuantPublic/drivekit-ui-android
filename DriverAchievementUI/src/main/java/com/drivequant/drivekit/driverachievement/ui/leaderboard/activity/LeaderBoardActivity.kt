package com.drivequant.drivekit.driverachievement.ui.leaderboard.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment.LeaderBoardFragment

class LeaderBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_leaderboard)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, LeaderBoardFragment())
            .commit()
    }
}