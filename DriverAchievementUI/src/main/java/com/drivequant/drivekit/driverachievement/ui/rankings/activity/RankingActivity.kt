package com.drivequant.drivekit.driverachievement.ui.rankings.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.rankings.fragment.RankingFragment

class RankingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_ranking)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, RankingFragment())
            .commit()
    }
}