package com.drivequant.drivekit.ui.tripdetail.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.TripsViewConfig
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment

class TripDetailActivity : AppCompatActivity() {

    companion object {
        private const val ITINID_EXTRA = "itinId-extra"
        private const val OPEN_ADVICE_EXTRA = "openAdvice-extra"
        private lateinit var tripDetailViewConfig: TripDetailViewConfig
        private lateinit var tripsViewConfig: TripsViewConfig

        fun launchActivity(context: Context,
                           itinId : String,
                           tripDetailViewConfig: TripDetailViewConfig = TripDetailViewConfig(context),
                           tripsViewConfig: TripsViewConfig,
                           openAdvice: Boolean = false) {
            TripDetailActivity.tripDetailViewConfig = tripDetailViewConfig
            TripDetailActivity.tripsViewConfig = tripsViewConfig
            val intent = Intent(context, TripDetailActivity::class.java)
            intent.putExtra(ITINID_EXTRA, itinId)
            intent.putExtra(OPEN_ADVICE_EXTRA, openAdvice)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.setBackgroundDrawable(ColorDrawable(DriveKitUI.colors.primaryColor()))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val itinId = intent.getStringExtra(ITINID_EXTRA) as String
        val openAdvice = intent.getBooleanExtra(OPEN_ADVICE_EXTRA, false)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,
                TripDetailFragment.newInstance(itinId, tripDetailViewConfig, tripsViewConfig, openAdvice))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
