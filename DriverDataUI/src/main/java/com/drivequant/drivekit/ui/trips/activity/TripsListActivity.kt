package com.drivequant.drivekit.ui.trips.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.fragment.TripsListFragment
import kotlinx.android.synthetic.main.fragment_trips_list.*

class TripsListActivity : AppCompatActivity() {

    lateinit var fragment: TripsListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips_list)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        DriveKitNavigationController.driverDataUIEntryPoint?.let {
            fragment = it.createTripListFragment() as TripsListFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == TripDetailActivity.UPDATE_TRIPS_REQUEST_CODE) {
                fragment.apply {
                    updateTrips(SynchronizationType.CACHE)
                    filter_view_vehicle.spinner.setSelection(0, false)
                }
            }
        }
    }
}