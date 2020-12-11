package com.drivequant.drivekit.ui.transportationmode.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment

internal class TransportationModeActivity : AppCompatActivity() {

    companion object {
        private const val ITINID_EXTRA = "itinId-extra"
        const val UPDATE_TRIP_DECLARATION_MODE = 104

        fun launchActivity(
            activity: Activity,
            itinId: String
        ) {
            val intent = Intent(activity, TransportationModeActivity::class.java)
            intent.putExtra(ITINID_EXTRA, itinId)
            activity.startActivityForResult(intent, UPDATE_TRIP_DECLARATION_MODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_transportation_mode_activity)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = DKResource.convertToString(this, "dk_driverdata_transportation_mode_title")

        val itinId = intent.getStringExtra(ITINID_EXTRA) as String
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,
                TripDetailFragment.newInstance(itinId, openAdvice, tripListConfigurationType))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}