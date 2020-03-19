package com.drivequant.drivekit.vehicle.ui.beacon.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.beaconutils.BeaconData
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel

class BeaconActivity : AppCompatActivity() {
    private lateinit var viewModel : BeaconViewModel
    private lateinit var scanType : BeaconScanType
    private lateinit var vehicleId : String
    private var beacon : BeaconData? = null

    companion object {
        private const val SCAN_TYPE_EXTRA = "scan-type-extra"
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
        private const val BEACON_EXTRA = "beacon-extra"

        fun launchActivity(context: Context,
                           scanType: BeaconScanType,
                           vehicleId: String,
                           beacon: BeaconData? = null
        ) {
            val intent = Intent(context, BeaconActivity::class.java)
            intent.putExtra(SCAN_TYPE_EXTRA, scanType)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            intent.putExtra(BEACON_EXTRA, beacon)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.setBackgroundDrawable(ColorDrawable(DriveKitUI.colors.primaryColor()))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        scanType = intent.getSerializableExtra(SCAN_TYPE_EXTRA) as BeaconScanType
        vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA) as String
        beacon = intent.getSerializableExtra(BEACON_EXTRA) as BeaconData?

        viewModel = ViewModelProviders.of(this,
            BeaconViewModel.BeaconViewModelFactory(scanType, vehicleId, beacon)).get(BeaconViewModel::class.java)

        viewModel.fragmentDispatcher.observe(this, Observer { fragment ->
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_left, R.animator.slide_out_right)
                    .addToBackStack(fragment.javaClass.name)
                    .add(R.id.container, it)
                    .commit()
            }
        })

        // TODO en fonction du mode (verify, pair, diag
        this.title = DKResource.convertToString(this, "dk_beacon_paired_title")
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1){
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}