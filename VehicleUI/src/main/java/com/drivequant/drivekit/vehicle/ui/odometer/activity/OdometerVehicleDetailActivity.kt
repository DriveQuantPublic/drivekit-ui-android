package com.drivequant.drivekit.vehicle.ui.odometer.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.fragment.OdometerVehicleDetailFragment

class OdometerVehicleDetailActivity : AppCompatActivity() {

    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicle-id-extra"

        fun launchActivity(
            context: Context,
            vehicleId: String) {
            val intent = Intent(context, OdometerVehicleDetailActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_odometer_vehicle_detail)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //TODO Update activity title
        title = DKResource.convertToString(this, "dk_challenge_menu")

        val vehicleId =
            intent.getStringExtra(VEHICLE_ID_EXTRA) as String

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, OdometerVehicleDetailFragment.newInstance(vehicleId))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}