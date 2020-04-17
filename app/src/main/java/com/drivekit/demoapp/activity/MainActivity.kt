package com.drivekit.demoapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.drivekit.demoapp.utils.PermissionUtils
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.core.DriveKitSharedPreferencesUtils
import com.drivequant.drivekit.permissionsutils.PermissionUtilsUI
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionType
import com.drivequant.drivekit.permissionsutils.diagnosis.model.SensorType
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val permissionUtils = PermissionUtils()
    private val REQUEST_LOCATION = 0
    private val REQUEST_LOGGING = 1
    private val REQUEST_ACTIVITY = 2

    private var menu: Menu? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(dk_toolbar)
    }

    override fun onResume() {
        super.onResume()
        handlePermissionButtonsVisibility()
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

    fun onDriverDataClicked(view: View){
        DriveKitNavigationController.driverDataUIEntryPoint?.startTripListActivity(applicationContext)
    }

    fun onDriverAchievementClicked(view: View) {
        DriveKitNavigationController.driverAchievementUIEntryPoint?.startStreakListActivity(applicationContext)
    }

    fun onPermissionUtilsClicked(view: View) {
        val permissionViews = arrayListOf(
            PermissionView.ACTIVITY,
            PermissionView.LOCATION,
            PermissionView.BACKGROUND_TASK
        )
        PermissionUtilsUI.showPermissionViews(
            this@MainActivity,
            permissionViews,
            object :
                PermissionViewListener {
                override fun onFinish() {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                }
            })
    }

    fun buttonSensorsClicked(view: View) {
        when (view.id) {
            R.id.button_gps ->
                Toast.makeText(
                    this,
                    "GPS Enable : ${DiagnosisHelper.isLocationSensorHighAccuracy(
                        this,
                        DiagnosisHelper.isSensorActivated(this, SensorType.GPS)
                    )}",
                    Toast.LENGTH_SHORT
                ).show()
            R.id.button_bluetooth ->
                Toast.makeText(
                    this,
                    "BLUETOOTH Enable : ${DiagnosisHelper.isSensorActivated(
                        this,
                        SensorType.BLUETOOTH
                    )}",
                    Toast.LENGTH_SHORT
                ).show()
            R.id.button_network -> Toast.makeText(
                this,
                "Network Enable : ${DiagnosisHelper.isNetworkReachable(this)}",
                Toast.LENGTH_SHORT
            ).show()

            R.id.button_notification -> Toast.makeText(
                this,
                "Notification Enable : ${DiagnosisHelper.getPermissionStatus(
                    this,
                    PermissionType.NOTIFICATION
                )}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun buttonTripClicked(view: View){
        if (DriveKitTripAnalysis.isConfigured()) {
            when (view.id) {
                R.id.button_trip_start -> {
                    DriveKitTripAnalysis.startTrip()
                }
                R.id.button_trip_stop -> {
                    DriveKitTripAnalysis.stopTrip()
                }
                R.id.button_trip_cancel -> {
                    DriveKitTripAnalysis.cancelTrip()
                }
            }
        }
    }

    fun onVehicleClicked(view: View){
        if (DriveKitTripAnalysis.isConfigured()) {
            when (view.id){
                R.id.button_vehicle_picker -> {
                    VehiclePickerActivity.launchActivity(this)
                }

                R.id.button_vehicle_list -> {
                    DriveKitNavigationController.vehicleUIEntryPoint?.startVehicleListActivity(this)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    handlePermissionButtonsVisibility()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                            launchSettingsIntent()
                        }
                    } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        launchSettingsIntent()
                    }
                }
                return
            }
            REQUEST_ACTIVITY -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    handlePermissionButtonsVisibility()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACTIVITY_RECOGNITION)){
                            launchSettingsIntent()
                        }
                    }
                }
            }
            REQUEST_LOGGING -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    handlePermissionButtonsVisibility()
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        launchSettingsIntent()
                    }
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

    private fun launchSettingsIntent(){
        val intent = Intent()
        val packageName = packageName
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }
}
