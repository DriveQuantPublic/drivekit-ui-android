package com.drivekit.demoapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.ui.DriverDataUI

class LastTripsCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_trips_card)

        val fragment = DriverDataUI.getLastTripsView(HeaderDay.DURATION)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}