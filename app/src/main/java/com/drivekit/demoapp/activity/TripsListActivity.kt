package com.drivekit.demoapp.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.TripsViewConfig
import com.drivequant.drivekit.ui.tripdetail.viewmodel.HeaderDay
import com.drivequant.drivekit.ui.trips.fragment.TripsListFragment
import com.drivequant.drivekit.ui.trips.viewmodel.TripData
import com.drivequant.drivekit.ui.trips.viewmodel.TripInfo

class TripsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips_list)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val tripsViewConfig = TripsViewConfig(
            this,
            TripData.SAFETY,
            TripInfo.NONE,
            HeaderDay.DURATION_DISTANCE,
            true
        )

        val tripDetailViewConfig = TripDetailViewConfig(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, TripsListFragment.newInstance(
                tripsViewConfig,
                tripDetailViewConfig
            ))
            .commit()
    }
}