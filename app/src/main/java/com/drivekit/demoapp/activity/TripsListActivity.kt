package com.drivekit.demoapp.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.trips.fragment.TripsListFragment

class TripsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips_list)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, TripsListFragment())
            .commit()
    }
}