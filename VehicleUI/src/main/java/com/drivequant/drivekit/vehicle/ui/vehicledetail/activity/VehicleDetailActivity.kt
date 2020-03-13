package com.drivequant.drivekit.vehicle.ui.vehicledetail.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.vehicle.ui.R

class VehicleDetailActivity : AppCompatActivity() {

    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        DriveKitNavigationController.vehicleUIEntryPoint?.let {
            val vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA) as String
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,
                    it.createVehicleDetailFragment(vehicleId))
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}