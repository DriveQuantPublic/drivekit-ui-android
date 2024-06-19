package com.drivequant.drivekit.vehicle.ui.beacon.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.core.extension.getSerializableExtraCompat
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconDetailFragment
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconDetailViewModel
import com.drivequant.drivekit.vehicle.ui.utils.DKBeaconRetrievedInfo
import com.drivequant.drivekit.vehicle.ui.utils.NearbyDevicesUtils

class BeaconDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: BeaconDetailViewModel
    private var vehicleId: String? = null
    private var vehicleName: String? = null
    private var beaconRetrievedInfo: DKBeaconRetrievedInfo? = null
    private var beaconInfo: BeaconInfo? = null

    companion object  {
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
        private const val VEHICLE_NAME_EXTRA = "vehicleName-extra"
        private const val BEACON_INFO_EXTRA = "beacon-info-extra"
        private const val BEACON_RETRIEVED_INFO_EXTRA = "beacon-retrieved-info-extra"

        fun launchActivity(context: Context,
                           vehicleId: String,
                           vehicleName: String,
                           beaconRetrievedInfo: DKBeaconRetrievedInfo,
                           beaconInfo: BeaconInfo) {
            val intent = Intent(context, BeaconDetailActivity::class.java)
            intent.apply {
                putExtra(VEHICLE_ID_EXTRA, vehicleId)
                putExtra(VEHICLE_NAME_EXTRA, vehicleName)
                putExtra(BEACON_INFO_EXTRA, beaconInfo)
                putExtra(BEACON_RETRIEVED_INFO_EXTRA, beaconRetrievedInfo)
                context.startActivity(intent)
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_vehicles_beacon_info), javaClass.simpleName)

        setContentView(R.layout.activity_beacon)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(DKColors.primaryColor))
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA)
        vehicleName = intent.getStringExtra(VEHICLE_NAME_EXTRA)
        beaconInfo = intent.getSerializableExtraCompat(BEACON_INFO_EXTRA, BeaconInfo::class.java)
        beaconRetrievedInfo = intent.getSerializableExtraCompat(BEACON_RETRIEVED_INFO_EXTRA, DKBeaconRetrievedInfo::class.java)

        vehicleId?.let { vehicleId ->
            beaconInfo?.let { beaconInfo ->
                beaconRetrievedInfo?.let { beaconRetrievedInfo ->
                    vehicleName?.let { vehicleName ->
                        viewModel = ViewModelProvider(
                            this,
                            BeaconDetailViewModel.BeaconDetailViewModelFactory(
                                vehicleId,
                                vehicleName,
                                beaconRetrievedInfo,
                                beaconInfo
                            )
                        )[BeaconDetailViewModel::class.java]
                    }
                }
            }
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.slide_in_left,
                R.animator.slide_out_right,
                R.animator.slide_in_left,
                R.animator.slide_out_right
            )
            .replace(R.id.container, BeaconDetailFragment.newInstance(viewModel))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NearbyDevicesUtils.NEARBY_DEVICES_PERMISSIONS_REQUEST_CODE) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) || !ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            ) {
                NearbyDevicesUtils.displayPermissionsError(this, true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(getString(R.string.dk_beacon_diagnostic_title))
    }
}
