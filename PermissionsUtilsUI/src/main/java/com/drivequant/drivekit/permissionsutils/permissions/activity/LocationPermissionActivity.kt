package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
import kotlinx.android.synthetic.main.activity_location_permission.*

class LocationPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_permission)
        setToolbar("dk_perm_utils_permissions_location_title")
        setStyle()

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (DiagnosisHelper.hasCoarseLocationPermission(this)) {
                    text_view_location_permission_text1.text =
                        DKResource.convertToString(this,"dk_perm_utils_app_diag_location_ko_android12")
                } else {
                    text_view_location_permission_text1.text =
                        DKResource.convertToString(this,"dk_perm_utils_permissions_location_text1_android12")
                    text_view_location_permission_text2.text =
                        DKResource.convertToString(this,"dk_perm_utils_permissions_location_text2_android12")
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                text_view_location_permission_text1.text =
                    DKResource.convertToString(this,"dk_perm_utils_permissions_location_text1_android11")
                text_view_location_permission_text2.text =
                    DKResource.convertToString(this,"dk_perm_utils_permissions_location_text2_android11")
            }
            else -> {
                val stringResId = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                        "dk_perm_utils_permissions_location_text2_post_android10"
                    else -> "dk_perm_utils_permissions_location_text2_pre_android10"
                }
                text_view_location_permission_text2.text = DKResource.convertToString(this, stringResId)
            }
        }
    }

    fun onRequestPermissionClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        if (DiagnosisHelper.hasFineLocationPermission(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && permissionCallback != null) {
                request(this,
                    permissionCallback as OnPermissionCallback,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                checkRequiredPermissions()
            }
        } else {
            checkRequiredPermissions()
        }
    }

    private fun checkRequiredPermissions() {
        permissionCallback = object : OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (permissionName.size == 1 && permissionName.first() == Manifest.permission.ACCESS_FINE_LOCATION) {
                        checkRequiredPermissions()
                    } else {
                        forward()
                    }
                } else {
                    forward()
                }
            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
                handlePermissionDeclined(
                    this@LocationPermissionActivity,
                    R.string.dk_perm_utils_app_diag_location_ko_android,
                    this@LocationPermissionActivity::checkRequiredPermissions)
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                    button_request_location_permission.text =
                        getString(R.string.dk_perm_utils_permissions_text_button_location_settings)
                    handlePermissionTotallyDeclined(
                        this@LocationPermissionActivity,
                        R.string.dk_perm_utils_app_diag_location_ko_android)
            }
        }

        if (DiagnosisHelper.hasFineLocationPermission(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (DiagnosisHelper.hasBackgroundLocationApproved(this)) {
                    forward()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        text_view_location_permission_text1.text = DKResource.convertToString(
                            this,
                            "dk_perm_utils_permissions_location_text3_android11"
                        )
                        val alwaysLabel = packageManager.backgroundPermissionOptionLabel
                        text_view_location_permission_text2.text = DKResource.buildString(
                            this,
                            DriveKitUI.colors.mainFontColor(),
                            DriveKitUI.colors.mainFontColor(),
                            "dk_perm_utils_permissions_location_text4_android11",
                            "$alwaysLabel"
                        )
                        button_request_location_permission.text = DKResource.convertToString(
                            this,
                            "dk_perm_utils_permissions_text_button_location_settings"
                        )
                    } else {
                        request(this,
                            permissionCallback as OnPermissionCallback,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    }
                }
            } else {
                forward()
            }
        } else {
            when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.Q -> {
                    request(
                        this,
                        permissionCallback as OnPermissionCallback,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
                else -> {
                    request(
                        this,
                        permissionCallback as OnPermissionCallback,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            checkRequiredPermissions()
        }
    }

    private fun setStyle() {
        text_view_permission_location_title.highlightMedium()
        text_view_location_permission_text1.normalText()
        text_view_location_permission_text2.normalText()
        button_request_location_permission.button()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
