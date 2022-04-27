package com.drivekit.demoapp.splashscreen.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.onboarding.activity.ApiKeyActivity
import com.drivekit.demoapp.splashscreen.viewmodel.SplashScreenViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView

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

        viewModel.syncFinished.observe(this, {
            val permissions: ArrayList<PermissionView> = ArrayList()
            permissions.addAll(PermissionView.values())
            PermissionsUtilsUI.showPermissionViews(
                this,
                permissions,
                object : PermissionViewListener {
                    override fun onFinish() {
                        DashboardActivity.launchActivity(this@SplashScreenActivity)
                        finish()
                    }
                })
        })
    }
}