package com.drivequant.drivekit.ui.tripdetail.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.transportationmode.activity.TransportationModeActivity
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType

class TripDetailActivity : AppCompatActivity() {

    private var shouldRefreshTrips = false

    companion object {
        const val ITINID_EXTRA = "itinId-extra"
        const val OPEN_ADVICE_EXTRA = "openAdvice-extra"
        const val TRIP_LIST_CONFIGURATION_TYPE_EXTRA = "tripListConfiguration-extra"
        const val UPDATE_TRIPS_REQUEST_CODE = 103

        fun launchActivity(
            activity: Activity,
            itinId: String,
            openAdvice: Boolean = false,
            tripListConfigurationType: TripListConfigurationType = TripListConfigurationType.MOTORIZED,
            parentFragment: Fragment? = null
        ) {
            val intent = Intent(activity, TripDetailActivity::class.java)
            intent.putExtra(ITINID_EXTRA, itinId)
            intent.putExtra(OPEN_ADVICE_EXTRA, openAdvice)
            intent.putExtra(TRIP_LIST_CONFIGURATION_TYPE_EXTRA, tripListConfigurationType)
            parentFragment?.let {
                parentFragment.startActivityForResult(intent, UPDATE_TRIPS_REQUEST_CODE)
            }?: run {
                activity.startActivityForResult(intent, UPDATE_TRIPS_REQUEST_CODE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = DKResource.convertToString(this, "dk_driverdata_trip_detail_title")

        val itinId = intent.getStringExtra(ITINID_EXTRA) as String
        val openAdvice = intent.getBooleanExtra(OPEN_ADVICE_EXTRA, false)
        val tripListConfigurationType = intent.getSerializableExtra(TRIP_LIST_CONFIGURATION_TYPE_EXTRA) as TripListConfigurationType? ?: TripListConfigurationType.MOTORIZED
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,
                TripDetailFragment.newInstance(itinId, openAdvice, tripListConfigurationType))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (shouldRefreshTrips){
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == TransportationModeActivity.UPDATE_TRIP_TRANSPORTATION_MODE){
            shouldRefreshTrips = true
        }
    }
}
