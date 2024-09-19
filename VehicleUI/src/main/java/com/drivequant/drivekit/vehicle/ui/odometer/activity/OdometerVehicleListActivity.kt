package com.drivequant.drivekit.vehicle.ui.odometer.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.fragment.OdometerVehicleListFragment

class OdometerVehicleListActivity : AppCompatActivity() {

    companion object {
        const val VEHICLE_ID_EXTRA = "vehicle-id-extra"

        fun launchActivity(
            activity: Activity,
            vehicleId: String? = null) {
            val intent = Intent(activity, OdometerVehicleListActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
        }

        fun launchActivity(
            context: Context,
            vehicleId: String? = null) {
            val intent = Intent(context, OdometerVehicleListActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_odometer_vehicle_list)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, OdometerVehicleListFragment.newInstance(vehicleId))
            .commit()

        DKEdgeToEdgeManager.apply {
            addSystemStatusBarTopPadding(findViewById(R.id.toolbar))
            addSystemNavigationBarBottomMargin(findViewById(R.id.container))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(getString(R.string.dk_vehicle_odometer))
    }
}
