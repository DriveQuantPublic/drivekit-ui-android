package com.drivequant.drivekit.vehicle.ui.bluetooth.activity

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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.bluetooth.viewmodel.BluetoothViewModel
import com.drivequant.drivekit.vehicle.ui.databinding.ActivityBluetoothBinding
import com.drivequant.drivekit.vehicle.ui.utils.NearbyDevicesUtils

class BluetoothActivity : AppCompatActivity() {
    private lateinit var viewModel: BluetoothViewModel
    private lateinit var vehicleId: String
    private lateinit var vehicleName: String
    private lateinit var binding: ActivityBluetoothBinding

    companion object {
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
        private const val VEHICLE_NAME_EXTRA = "vehicleName-extra"

        fun launchActivity(context: Context, vehicleId: String, vehicleName: String) {
            val intent = Intent(context, BluetoothActivity::class.java)
            intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
            intent.putExtra(VEHICLE_NAME_EXTRA, vehicleName)
            context.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_vehicles_bluetooth_add), javaClass.simpleName)

        binding = ActivityBluetoothBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(binding.root.findViewById(R.id.dk_toolbar))

        supportActionBar?.setBackgroundDrawable(ColorDrawable(DKColors.primaryColor))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA) as String
        vehicleName = intent.getStringExtra(VEHICLE_NAME_EXTRA) as String

        viewModel = ViewModelProvider(this, BluetoothViewModel.BluetoothViewModelFactory(vehicleId, vehicleName))[BluetoothViewModel::class.java]

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
                    .add(R.id.container, it)
                    .commit()
            }
        }

        viewModel.nearbyDevicesAlertDialogObserver.observe(this) {
            it?.let { displayError ->
                if (displayError) {
                    NearbyDevicesUtils.displayPermissionsError(this@BluetoothActivity)
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

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(binding.root) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1){
            finish()
        } else {
            supportFragmentManager.popBackStack()
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
                viewModel.onStartButtonClicked()
            } else if (!shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN) || !shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
                NearbyDevicesUtils.displayPermissionsError(this, true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(getString(R.string.dk_vehicle_bluetooth_combination_view_title))
    }
}
