package com.drivequant.drivekit.vehicle.ui.odometer.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.fragment.OdometerHistoriesListFragment

class OdometerHistoriesListActivity : AppCompatActivity() {


    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicle-id-extra"

        fun launchActivity(
            context: Context,
            vehicleId: String) {
            val intent = Intent(context, OdometerHistoriesListActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_odometer_histories_list)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = DKResource.convertToString(this, "dk_vehicle_odometer_references_title")

        val vehicleId =
            intent.getStringExtra(VEHICLE_ID_EXTRA) as String

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, OdometerHistoriesListFragment.newInstance(vehicleId))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}