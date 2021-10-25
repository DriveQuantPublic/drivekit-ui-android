package com.drivequant.drivekit.vehicle.ui.odometer.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.fragment.OdometerVehicleListFragment

class OdometerVehicleListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_odometer_vehicle_list)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = DKResource.convertToString(this, "dk_vehicle_odometer_vehicle_title")
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, OdometerVehicleListFragment.newInstance())
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}