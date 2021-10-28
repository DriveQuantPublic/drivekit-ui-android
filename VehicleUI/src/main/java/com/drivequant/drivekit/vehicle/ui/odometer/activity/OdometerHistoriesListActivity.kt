package com.drivequant.drivekit.vehicle.ui.odometer.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.fragment.OdometerHistoriesListFragment

class OdometerHistoriesListActivity : AppCompatActivity() {

    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicle-id-extra"
        const val UPDATE_VEHICLE_ODOMETER_DETAIL_REQUEST_CODE = 98


        fun launchActivity(
            activity: Activity,
            vehicleId: String,
            parentFragment: Fragment? = null) {
            val intent = Intent(activity, OdometerHistoriesListActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            parentFragment?.startActivityForResult(intent, UPDATE_VEHICLE_ODOMETER_DETAIL_REQUEST_CODE)
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