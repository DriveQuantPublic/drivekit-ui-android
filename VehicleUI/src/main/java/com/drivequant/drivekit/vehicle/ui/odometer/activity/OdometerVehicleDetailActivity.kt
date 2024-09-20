package com.drivequant.drivekit.vehicle.ui.odometer.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.fragment.OdometerVehicleDetailFragment

class OdometerVehicleDetailActivity : AppCompatActivity() {

    private lateinit var fragment: OdometerVehicleDetailFragment

    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicle-id-extra"
        const val UPDATE_VEHICLE_ODOMETER_LIST_REQUEST_CODE = 97

        fun launchActivity(
            activity: Activity,
            vehicleId: String,
            parentFragment: Fragment? = null) {
            val intent = Intent(activity, OdometerVehicleDetailActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            parentFragment?.startActivityForResult(intent, UPDATE_VEHICLE_ODOMETER_LIST_REQUEST_CODE)
                ?: activity.startActivityForResult(intent, UPDATE_VEHICLE_ODOMETER_LIST_REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_odometer_vehicle_detail)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA) as String

        fragment = OdometerVehicleDetailFragment.newInstance(vehicleId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundDarkColor(window)
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
        setActivityTitle(getString(R.string.dk_vehicle_odometer_vehicle_title))
    }
}
