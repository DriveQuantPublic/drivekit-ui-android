package com.drivekit.demoapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.challenge.ui.ChallengeUI
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.badges.activity.BadgeListActivity
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.listener.VehiclePickerCompleteListener
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var menu: Menu? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(dk_toolbar)
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

    fun onDriverDataSynthesisCardsClicked(view: View){
        startActivity(Intent(this, SynthesisCardExampleActivity::class.java))
    }

    fun onDriverDataLastTripsCardsClicked(view: View) {
        startActivity(Intent(this, LastTripsCardActivity::class.java))
    }

    fun onTripAnalysisActivationHoursClicked(view: View) {
        DriveKitNavigationController.tripAnalysisUIEntryPoint?.startActivationHoursActivity(applicationContext)
    }

    fun onDriverStreaksClicked(view: View) {
        DriveKitNavigationController.driverAchievementUIEntryPoint?.startStreakListActivity(applicationContext)
    }

    fun onDriverBadgesClicked(view: View) {
        val intent = Intent(this, BadgeListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun onDriverRankingClicked(view: View) {
        DriverAchievementUI.startRankingActivity(this)
    }

    fun onChallengeClicked(view: View) {
        ChallengeUI.startChallengeActivity(this)
    }

    fun onPermissionUtilsClicked(view: View) {
        val permissionViews = arrayListOf(
            PermissionView.LOCATION,
            PermissionView.ACTIVITY,
            PermissionView.BACKGROUND_TASK,
            PermissionView.NEARBY_DEVICES
        )
        PermissionsUtilsUI.showPermissionViews(
            this@MainActivity,
            permissionViews,
            object :
                PermissionViewListener {
                override fun onFinish() {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                }
            })
    }

    fun onAppDiagClicked(view: View) {
        DriveKitNavigationController.permissionsUtilsUIEntryPoint?.startAppDiagnosisActivity(this@MainActivity)
    }

    fun buttonTripClicked(view: View) {
        if (DriveKitTripAnalysis.isConfigured()) {
            when (view.id) {
                R.id.button_trip_start -> DriveKitTripAnalysis.startTrip()
                R.id.button_trip_stop -> DriveKitTripAnalysis.stopTrip()
                R.id.button_trip_cancel -> DriveKitTripAnalysis.cancelTrip()
            }
        }
    }

    fun onVehicleClicked(view: View) {
        if (DriveKitTripAnalysis.isConfigured()) {
            when (view.id) {
                R.id.button_vehicle_picker -> {
                    VehiclePickerActivity.launchActivity(
                        this,
                        listener = object : VehiclePickerCompleteListener {
                            override fun onVehiclePickerFinished(vehicleId: String) {
                                Log.e("VehiclePicker", "New vehicle created : $vehicleId")
                            }
                        })
                }

                R.id.button_vehicle_list -> {
                    DriveKitNavigationController.vehicleUIEntryPoint?.startVehicleListActivity(this)
                }
            }
        }
    }

    fun onOdometerVehicleClicked(view: View) {
        DriveKitVehicleUI.startOdometerUIActivity(this)
    }
}