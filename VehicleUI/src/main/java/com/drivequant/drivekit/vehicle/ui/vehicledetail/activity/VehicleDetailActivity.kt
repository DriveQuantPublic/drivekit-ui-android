package com.drivequant.drivekit.vehicle.ui.vehicledetail.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment.VehicleDetailFragment

class VehicleDetailActivity : AppCompatActivity() {

    companion object {
        private const val VEHICLEID_EXTRA = "vehicleId-extra"

        fun launchActivity(context: Context, vehicleId: String) {
            val intent = Intent(context, VehicleDetailActivity::class.java)
            intent.putExtra(VEHICLEID_EXTRA, vehicleId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.setBackgroundDrawable(ColorDrawable(DriveKitUI.colors.primaryColor()))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val vehicleId = intent.getStringExtra(VEHICLEID_EXTRA) as String
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,
                VehicleDetailFragment.newInstance(vehicleId))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}