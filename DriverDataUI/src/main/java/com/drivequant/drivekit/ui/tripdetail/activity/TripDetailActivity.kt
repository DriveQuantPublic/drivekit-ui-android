package com.drivequant.drivekit.ui.tripdetail.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment

class TripDetailActivity : AppCompatActivity() {

    companion object {
        private const val ITINID_EXTRA = "itinId-extra"
        private const val OPEN_ADVICE_EXTRA = "openAdvice-extra"
        const val UPDATE_TRIPS_REQUEST_CODE = 103

        fun launchActivity(activity: Activity,
                           itinId : String,
                           openAdvice: Boolean = false) {
            val intent = Intent(activity, TripDetailActivity::class.java)
            intent.putExtra(ITINID_EXTRA, itinId)
            intent.putExtra(OPEN_ADVICE_EXTRA, openAdvice)
            activity.startActivityForResult(intent, UPDATE_TRIPS_REQUEST_CODE)
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,
                TripDetailFragment.newInstance(itinId, openAdvice))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
