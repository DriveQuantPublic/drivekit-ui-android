package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.databinding.ActivityNearbyDevicesPermissionBinding
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback

@RequiresApi(Build.VERSION_CODES.S)
class NearbyDevicesPermissionActivity : BasePermissionActivity() {

    private lateinit var binding: ActivityNearbyDevicesPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearbyDevicesPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar(R.string.dk_perm_utils_app_diag_nearby_title)
        setStyle()
    }

    fun onRequestPermissionClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        checkRequiredPermissions()
    }

    private fun checkRequiredPermissions() {
        permissionCallback = object : OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                forward()
            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
                handlePermissionDeclined(
                    this@NearbyDevicesPermissionActivity,
                    com.drivequant.drivekit.common.ui.R.string.dk_common_app_diag_nearby_ko,
                    this@NearbyDevicesPermissionActivity::checkRequiredPermissions
                )
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                binding.buttonRequestNearbyDevicesPermission.setText(R.string.dk_perm_utils_permissions_text_button_nearby_devices_settings)
                handlePermissionTotallyDeclined(
                    this@NearbyDevicesPermissionActivity,
                    com.drivequant.drivekit.common.ui.R.string.dk_common_app_diag_nearby_ko
                )
            }
        }

        request(
            this,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            checkRequiredPermissions()
        }
    }

    private fun setStyle() {
        binding.textViewNearbyDevicesPermissionTitle.highlightMedium()
        binding.textViewNearbyDevicesPermissionText1.normalText()
        binding.textViewNearbyDevicesPermissionText2.normalText()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
