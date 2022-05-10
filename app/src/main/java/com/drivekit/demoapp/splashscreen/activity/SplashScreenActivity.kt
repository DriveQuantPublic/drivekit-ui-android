package com.drivekit.demoapp.splashscreen.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drivekit.demoapp.config.DriveKitConfig
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.onboarding.activity.ApiKeyActivity
import com.drivekit.demoapp.onboarding.activity.VehiclesActivity
import com.drivekit.demoapp.splashscreen.viewmodel.SplashScreenViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel = SplashScreenViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (DriveKit.isUserConnected()) {
            viewModel.syncDriveKitModules()
        } else {
            ApiKeyActivity.launchActivity(this)
            finish()
        }

        viewModel.syncFinished.observe(this) {
            PermissionsUtilsUI.showPermissionViews(
                this,
                DriveKitConfig.getPermissionsViews(),
                object : PermissionViewListener {
                    override fun onFinish() {
                        viewModel.shouldShowVehicles()
                    }
                })
        }

        viewModel.shouldShowVehicles.observe(this) {
            if (it) {
                VehiclesActivity.launchActivity(this)
            } else {
                DashboardActivity.launchActivity(this)
            }
            finish()
        }
    }
}