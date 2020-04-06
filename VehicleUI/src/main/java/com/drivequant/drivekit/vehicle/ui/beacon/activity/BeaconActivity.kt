package com.drivequant.drivekit.vehicle.ui.beacon.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.activity_beacon.progress_circular
import kotlinx.android.synthetic.main.activity_vehicle_detail.*

class BeaconActivity : AppCompatActivity() {
    private lateinit var viewModel : BeaconViewModel
    private lateinit var scanType : BeaconScanType

    private var vehicleId : String? = null
    private var beacon : Beacon? = null

    companion object {
        private const val SCAN_TYPE_EXTRA = "scan-type-extra"
        private const val VEHICLE_ID_EXTRA = "vehicleId-extra"
        private const val BEACON_EXTRA = "beacon-extra"

        fun launchActivity(context: Context,
                           scanType: BeaconScanType,
                           vehicleId: String? = null,
                           beacon: Beacon? = null
        ) {
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
        setContentView(R.layout.activity_beacon)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(DriveKitUI.colors.primaryColor()))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        scanType = intent.getSerializableExtra(SCAN_TYPE_EXTRA) as BeaconScanType
        vehicleId = intent.getStringExtra(VEHICLE_ID_EXTRA)
        beacon = intent.getSerializableExtra(BEACON_EXTRA) as Beacon?

        viewModel = ViewModelProviders.of(this,
            BeaconViewModel.BeaconViewModelFactory(scanType, vehicleId, beacon)).get(BeaconViewModel::class.java)
        viewModel.init(this)

        viewModel.fragmentDispatcher.observe(this, Observer { fragment ->
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_left, R.animator.slide_out_right)
                    .addToBackStack(fragment.javaClass.name)
                    .add(R.id.container, it)
                    .commit()
            }
        })

        viewModel.beaconDetailObserver.observe(this, Observer {
            viewModel.vehicleId?.let { vehicleId ->
                viewModel.seenBeacon?.let { seenBeacon ->
                    viewModel.vehicleName?.let {vehicleName ->
                        BeaconDetailActivity.launchActivity(this, vehicleId, vehicleName, viewModel.batteryLevel, seenBeacon)
                    }
                }
            }
        })

        viewModel.progressBarObserver.observe(this, Observer {
            it?.let { displayProgressCircular ->
                if (displayProgressCircular){
                    showProgressCircular()
                } else {
                    hideProgressCircular()
                }
            }
        })
    }

    fun updateTitle(title: String){
        toolbar.title = title
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1){
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun hideProgressCircular() {
        progress_circular.animate()
            .alpha(0f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progress_circular?.visibility = View.GONE
                }
            })
    }

    private fun showProgressCircular() {
        progress_circular.animate()
            .alpha(255f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progress_circular?.visibility = View.VISIBLE
                }
            })
    }
}