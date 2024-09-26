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
import com.drivequant.drivekit.vehicle.ui.odometer.fragment.OdometerHistoriesListFragment
import com.drivequant.drivekit.vehicle.ui.odometer.fragment.OdometerHistoryDetailFragment
import com.drivequant.drivekit.vehicle.ui.odometer.fragment.OdometerVehicleListFragment

class OdometerHistoryDetailActivity : AppCompatActivity() {

    private var historyId: Int = -1

    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicle-id-extra"
        private const val HISTORY_ID_EXTRA = "history-id-extra"
        const val UPDATE_VEHICLE_ODOMETER_LIST_REQUEST_CODE = 97
        const val UPDATE_VEHICLE_ODOMETER_DETAIL_REQUEST_CODE = 98
        const val UPDATE_VEHICLE_HISTORY_LIST_REQUEST_CODE = 96

        fun launchActivity(
            activity: Activity,
            vehicleId: String,
            historyId: Int,
            parentFragment: Fragment? = null) {
            val intent = Intent(activity, OdometerHistoryDetailActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            intent.putExtra(HISTORY_ID_EXTRA, historyId)
            val requestCode = when (parentFragment) {
                is OdometerVehicleListFragment -> UPDATE_VEHICLE_ODOMETER_LIST_REQUEST_CODE
                is OdometerHistoriesListFragment -> UPDATE_VEHICLE_HISTORY_LIST_REQUEST_CODE
                else ->  UPDATE_VEHICLE_ODOMETER_DETAIL_REQUEST_CODE
            }
            parentFragment?.startActivityForResult(intent, requestCode)
                ?: activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_odometer_history_detail)

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA) as String
        historyId = intent.getIntExtra(HISTORY_ID_EXTRA, -1)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, OdometerHistoryDetailFragment.newInstance(vehicleId, historyId)).commit()

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(findViewById(R.id.root)) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        if (historyId == -1) {
            R.string.dk_vehicle_odometer_history_add
        } else {
            R.string.dk_vehicle_odometer_history_update
        }.let {
            setActivityTitle(getString(it))
        }
    }
}
