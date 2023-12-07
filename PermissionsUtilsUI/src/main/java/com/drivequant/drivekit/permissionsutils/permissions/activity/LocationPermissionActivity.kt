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
import com.drivequant.drivekit.permissionsutils.databinding.ActivityLocationPermissionBinding
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback

class LocationPermissionActivity : BasePermissionActivity() {

    private lateinit var binding: ActivityLocationPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("dk_perm_utils_permissions_location_title")
        setStyle()

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (DiagnosisHelper.hasCoarseLocationPermission(this)) {
                    binding.textViewLocationPermissionText1.text =
                        DKResource.convertToString(this,"dk_perm_utils_app_diag_location_ko_android12")
                } else {
                    binding.textViewLocationPermissionText1.text =
                        DKResource.convertToString(this,"dk_perm_utils_permissions_location_text1_android12")
                    binding.textViewLocationPermissionText2.text =
                        DKResource.convertToString(this,"dk_perm_utils_permissions_location_text2_android12")
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                binding.textViewLocationPermissionText1.text =
                    DKResource.convertToString(this,"dk_perm_utils_permissions_location_text1_android11")
                binding.textViewLocationPermissionText2.text =
                    DKResource.convertToString(this,"dk_perm_utils_permissions_location_text2_android11")
            }
            else -> {
                val stringResId = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                        "dk_perm_utils_permissions_location_text2_post_android10"
                    else -> "dk_perm_utils_permissions_location_text2_pre_android10"
                }
                binding.textViewLocationPermissionText2.text = DKResource.convertToString(this, stringResId)
            }
        }
    }

    fun onRequestPermissionClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        if (DiagnosisHelper.hasFineLocationPermission(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && permissionCallback != null) {
                request(this,
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
                binding.buttonRequestLocationPermission.text = getString(R.string.dk_perm_utils_permissions_text_button_location_settings)
                handlePermissionTotallyDeclined(
                    this@LocationPermissionActivity,
                    R.string.dk_perm_utils_app_diag_location_ko_android
                )
            }
        }

        if (DiagnosisHelper.hasFineLocationPermission(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (DiagnosisHelper.hasBackgroundLocationApproved(this)) {
                    forward()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        binding.textViewLocationPermissionText1.text = DKResource.convertToString(
                            this,
                            "dk_perm_utils_permissions_location_text3_android11"
                        )
                        val alwaysLabel = packageManager.backgroundPermissionOptionLabel
                        binding.textViewLocationPermissionText2.text = DKResource.buildString(
                            this,
                            DriveKitUI.colors.mainFontColor(),
                            DriveKitUI.colors.mainFontColor(),
                            "dk_perm_utils_permissions_location_text4_android11",
                            "$alwaysLabel"
                        )
                        binding.buttonRequestLocationPermission.text = DKResource.convertToString(
                            this,
                            "dk_perm_utils_permissions_text_button_location_settings"
                        )
                    } else {
                        request(this,
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
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
                else -> {
                    request(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            checkRequiredPermissions()
        }
    }

    private fun setStyle() {
        binding.textViewPermissionLocationTitle.highlightMedium()
        binding.textViewLocationPermissionText1.normalText()
        binding.textViewLocationPermissionText2.normalText()
        binding.buttonRequestLocationPermission.button()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
