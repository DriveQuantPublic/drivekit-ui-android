package com.drivequant.drivekit.vehicle.ui.beacon.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.extension.getSerializableExtraCompat
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.databinding.ActivityBeaconBinding
import com.drivequant.drivekit.vehicle.ui.utils.DKBeaconRetrievedInfo
import com.drivequant.drivekit.vehicle.ui.utils.NearbyDevicesUtils

class BeaconActivity : AppCompatActivity() {
    private lateinit var viewModel: BeaconViewModel
    private lateinit var scanType: BeaconScanType

    private lateinit var binding: ActivityBeaconBinding

    private var vehicleId: String? = null
    private var beacon: Beacon? = null

    companion object {
        private const val SCAN_TYPE_EXTRA = "scan-type-extra"
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
        private const val BEACON_EXTRA = "beacon-extra"

        fun launchActivity(
            context: Context,
            scanType: BeaconScanType,
            vehicleId: String? = null,
            beacon: Beacon? = null
        ) {
            DriveKitLog.i(DriveKitVehicleUI.TAG, "Beacon scanner launched in ${scanType.name} mode, vehicleId=$vehicleId, major=${beacon?.major}, minor=${beacon?.minor}")

            val intent = Intent(context, BeaconActivity::class.java)
            intent.putExtra(SCAN_TYPE_EXTRA, scanType)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            intent.putExtra(BEACON_EXTRA, beacon)
            context.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeaconBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(binding.root.findViewById(R.id.dk_toolbar))

        supportActionBar?.setBackgroundDrawable(ColorDrawable(DriveKitUI.colors.primaryColor()))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        scanType = intent.getSerializableExtraCompat(SCAN_TYPE_EXTRA, BeaconScanType::class.java)!!
        vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA)
        beacon = intent.getSerializableExtraCompat(BEACON_EXTRA, Beacon::class.java)

        viewModel = ViewModelProvider(this,
            BeaconViewModel.BeaconViewModelFactory(scanType, vehicleId, beacon))[BeaconViewModel::class.java]
        viewModel.init(this)

        viewModel.fragmentDispatcher.observe(this) { fragment ->
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right
                    )
                    .addToBackStack(fragment.javaClass.name)
                    .replace(R.id.container, it)
                    .commit()
            }
        }

        viewModel.beaconDetailObserver.observe(this) {
            viewModel.vehicleId?.let { vehicleId ->
                viewModel.seenBeacon?.let { seenBeacon ->
                    viewModel.vehicleName?.let { vehicleName ->
                        BeaconDetailActivity.launchActivity(
                            this,
                            vehicleId,
                            vehicleName,
                            DKBeaconRetrievedInfo(
                                viewModel.batteryLevel,
                                viewModel.estimatedDistance,
                                viewModel.rssi,
                                viewModel.txPower
                            ),
                            seenBeacon
                        )
                    }
                }
            }
        }

        viewModel.progressBarObserver.observe(this) {
            it?.let { displayProgressCircular ->
                if (displayProgressCircular) {
                    showProgressCircular()
                } else {
                    hideProgressCircular()
                }
            }
        }

        val screenNameResId = when (viewModel.scanType) {
            BeaconScanType.PAIRING -> "dk_tag_vehicles_beacon_add"
            BeaconScanType.DIAGNOSTIC -> "dk_tag_vehicles_beacon_diagnosis"
            BeaconScanType.VERIFY -> "dk_tag_vehicles_beacon_verify"
        }
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(this, screenNameResId), javaClass.simpleName)
    }

    private fun updateTitle() = when (viewModel.scanType) {
        BeaconScanType.PAIRING -> "dk_beacon_paired_title"
        BeaconScanType.DIAGNOSTIC,
        BeaconScanType.VERIFY -> "dk_beacon_diagnostic_title"
    }.let {
        setActivityTitle(DKResource.convertToString(this, it))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun hideProgressCircular() {
        binding.root.findViewById<ProgressBar>(com.drivequant.drivekit.common.ui.R.id.dk_progress_circular).apply {
            animate()
                .alpha(0f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                    }
                })
        }
    }

    private fun showProgressCircular() {
        binding.root.findViewById<ProgressBar>(com.drivequant.drivekit.common.ui.R.id.dk_progress_circular).apply {
            animate()
                .alpha(1f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.VISIBLE
                    }
                })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NearbyDevicesUtils.NEARBY_DEVICES_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do nothing
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)
                || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)
            ) {
                NearbyDevicesUtils.displayPermissionsError(this, true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateTitle()
    }
}
