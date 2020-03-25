package com.drivequant.drivekit.vehicle.ui.beacon.activity

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconDetailFragment
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconDetailViewModel

class BeaconDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: BeaconDetailViewModel
    private var vehicleId: String? = null
    private var batterylevel: Int = 0
    private var beaconInfo: BeaconInfo? = null

    companion object  {
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
        private const val BATTERY_LEVEL_EXTRA = "battery-level-extra"
        private const val BEACON_INFO_EXTRA = "beacon-info-extra"

        fun launchActivity(context: Context,
            vehicleId: String,
            batterylevel: Int,
            beaconInfo: BeaconInfo
        ) {
            val intent = Intent(context, BeaconDetailActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            intent.putExtra(BATTERY_LEVEL_EXTRA, batterylevel)
            intent.putExtra(BEACON_INFO_EXTRA, beaconInfo)
            context.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon)
        title = DKResource.convertToString(this, "dk_beacon_diagnostic_title")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.setBackgroundDrawable(ColorDrawable(DriveKitUI.colors.primaryColor()))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        batterylevel = intent.getIntExtra(BATTERY_LEVEL_EXTRA, 0)
        vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA)
        beaconInfo = intent.getSerializableExtra(BEACON_INFO_EXTRA) as BeaconInfo?

        vehicleId?.let {vehicleId ->
            beaconInfo?.let { beaconInfo ->
                viewModel = ViewModelProviders.of(this,
                    BeaconDetailViewModel.BeaconDetailViewModelFactory(vehicleId, batterylevel, beaconInfo))
                    .get(BeaconDetailViewModel::class.java)
            }
        }

        viewModel.vehicle?.let { vehicle ->
            beaconInfo?.let { beaconInfo ->
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right
                    )
                    .replace(R.id.container, BeaconDetailFragment.newInstance(viewModel, vehicle, batterylevel, beaconInfo))
                    .commit()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
    }
}