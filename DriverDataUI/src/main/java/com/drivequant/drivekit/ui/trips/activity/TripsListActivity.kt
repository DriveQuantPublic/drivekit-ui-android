package com.drivequant.drivekit.ui.trips.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.ui.R

class TripsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips_list)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        DriveKitNavigationController.driverDataUIEntryPoint?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, it.createTripListFragment())
                .commit()
        }
    }
}