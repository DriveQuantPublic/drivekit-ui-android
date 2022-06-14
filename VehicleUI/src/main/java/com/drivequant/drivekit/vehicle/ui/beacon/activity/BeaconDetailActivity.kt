package com.drivequant.drivekit.vehicle.ui.beacon.activity

import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.BeaconDetailFragment
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconDetailViewModel
import com.drivequant.drivekit.vehicle.ui.utils.DKBeaconRetrievedInfo
import com.drivequant.drivekit.vehicle.ui.utils.NearbyDevicesUtils

class BeaconDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: BeaconDetailViewModel
    private var vehicleId: String? = null
    private var vehicleName: String? = null
    private var batteryLevel: Int = 0
    private var estimatedDistance: Double = 0.0
    private var rssi: Int = 0
    private var txPower: Int = 0
    private var beaconInfo: BeaconInfo? = null

    companion object  {
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
        private const val VEHICLE_NAME_EXTRA = "vehicleName-extra"
        private const val BATTERY_LEVEL_EXTRA = "battery-level-extra"
        private const val BEACON_INFO_EXTRA = "beacon-info-extra"
        private const val BEACON_RSSI_EXTRA = "beacon-rssi-extra"
        private const val BEACON_DISTANCE_EXTRA = "beacon-estimated-distance-extra"
        private const val BEACON_TX_POWER_EXTRA = "beacon-tx-power-extra"

        fun launchActivity(context: Context,
                           vehicleId: String,
                           vehicleName: String,
                           beaconRetrievedInfo: DKBeaconRetrievedInfo,
                           beaconInfo: BeaconInfo) {
            val intent = Intent(context, BeaconDetailActivity::class.java)
            intent.apply {
                putExtra(VEHICLE_ID_EXTRA, vehicleId)
                putExtra(VEHICLE_NAME_EXTRA, vehicleName)
                putExtra(BATTERY_LEVEL_EXTRA, beaconRetrievedInfo.batteryLevel)
                putExtra(BEACON_RSSI_EXTRA, beaconRetrievedInfo.rssi)
                putExtra(BEACON_DISTANCE_EXTRA, beaconRetrievedInfo.estimatedDistance)
                putExtra(BEACON_TX_POWER_EXTRA, beaconRetrievedInfo.txPower)
                putExtra(BEACON_INFO_EXTRA, beaconInfo)
                context.startActivity(intent)
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(this, "dk_tag_vehicles_beacon_info"), javaClass.simpleName)

        setContentView(R.layout.activity_beacon)
        title = DKResource.convertToString(this, "dk_beacon_diagnostic_title")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(DriveKitUI.colors.primaryColor()))
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        batteryLevel = intent.getIntExtra(BATTERY_LEVEL_EXTRA, 0)
        estimatedDistance = intent.getDoubleExtra(BEACON_DISTANCE_EXTRA, 0.0)
        rssi = intent.getIntExtra(BEACON_RSSI_EXTRA, 0)
        txPower = intent.getIntExtra(BEACON_TX_POWER_EXTRA, 0)
        vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA)
        vehicleName = intent.getStringExtra(VEHICLE_NAME_EXTRA)
        beaconInfo = intent.getSerializableExtra(BEACON_INFO_EXTRA) as BeaconInfo?

        vehicleId?.let {vehicleId ->
            beaconInfo?.let { beaconInfo ->
                vehicleName?.let { vehicleName ->
                    viewModel = ViewModelProviders.of(
                        this,
                        BeaconDetailViewModel.BeaconDetailViewModelFactory(
                            vehicleId,
                            vehicleName,
                            DKBeaconRetrievedInfo(
                                batteryLevel,
                                estimatedDistance,
                                rssi,
                                txPower
                            ),
                            beaconInfo
                        )
                    ).get(BeaconDetailViewModel::class.java)
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
}