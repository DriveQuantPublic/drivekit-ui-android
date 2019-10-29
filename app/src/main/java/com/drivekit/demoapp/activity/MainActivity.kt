package com.drivekit.demoapp.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.drivequant.drivekit.core.DriveKitSharedPreferencesUtils
import com.drivekit.demoapp.utils.PermissionUtils
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val permissionUtils = PermissionUtils()
    private val REQUEST_LOCATION = 0
    private val REQUEST_LOGGING = 1
    private val REQUEST_ACTIVITY = 2

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(toolbar)
        checkTripRunning()
    }

    override fun onResume() {
        super.onResume()
        handlePermissionButtonsVisibility()
        checkTripRunning()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_action_bar_menu, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun onTripsListButtonClicked(view: View){
        startActivity(Intent(this, TripsListActivity::class.java))
    }

    private fun checkTripRunning(){
        if (DriveKitTripAnalysis.isConfigured()) {
            if (DriveKitTripAnalysis.isTripRunning()) {
                button_trip.text = getString(R.string.trip_in_progress)
            } else {
                initTripButton()
            }
        } else {
            button_trip.visibility = View.GONE
        }
    }

    private fun initTripButton(){
        button_trip.text = getString(R.string.trip_start)
        button_trip.setOnClickListener {
            if (DriveKitTripAnalysis.isConfigured()) {
                if (!DriveKitTripAnalysis.isTripRunning()) {
                    DriveKitTripAnalysis.startTrip()
                    button_trip.text = getString(R.string.trip_in_progress)
                } else {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.trip_stop_confirm))
                        .setCancelable(true)
                        .setPositiveButton(R.string.dk_ok) { dialog, _ ->
                            DriveKitTripAnalysis.stopTrip()
                            dialog.dismiss()
                            initTripButton()
                        }
                        .setNegativeButton(R.string.dk_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    handlePermissionButtonsVisibility()
                }
                return
            }
            REQUEST_ACTIVITY -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    handlePermissionButtonsVisibility()
                }
            }
            REQUEST_LOGGING -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    handlePermissionButtonsVisibility()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun permissionClicked(view: View){
        when (view.id){
            R.id.button_location -> { permissionUtils.checkLocationPermission(this, REQUEST_LOCATION) }
            R.id.button_activity -> { permissionUtils.checkActivityRecognitionPermission(this, REQUEST_ACTIVITY) }
            R.id.button_battery -> { permissionUtils.checkBatteryOptimization(this) }
            R.id.button_storage -> { permissionUtils.checkLoggingPermission(this, REQUEST_LOGGING) }
        }
    }

    private fun handlePermissionButtonsVisibility(){
        if (!permissionUtils.isLocationAuthorize(this)){
            button_location.visibility = View.VISIBLE
        } else {
            button_location.visibility = View.GONE
        }

        if (!permissionUtils.isActivityRecognitionAuthorize(this)){
            button_activity.visibility = View.VISIBLE
        } else {
            button_activity.visibility = View.GONE
        }

        if (!permissionUtils.isIgnoringBatteryOptimizations(this)){
            button_battery.visibility = View.VISIBLE
        } else {
            button_battery.visibility = View.GONE
        }

        val logPath = DriveKitSharedPreferencesUtils.contains("drivekit-logging")
        if (logPath && !permissionUtils.isLoggingAuthorize(this)){
            button_storage.visibility = View.VISIBLE
        } else {
            button_storage.visibility = View.GONE
        }
    }
}
