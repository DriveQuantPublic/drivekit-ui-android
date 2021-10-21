package com.drivekit.demoapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.vehicle.ui.odometer.fragment.OdometerVehicleListFragment

class OdometerVehicleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_odometer_vehicle)

        val fragment = OdometerVehicleListFragment.newInstance()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}