package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
import kotlinx.android.synthetic.main.activity_nearby_devices_permission.*

@RequiresApi(Build.VERSION_CODES.S)
class NearbyDevicesPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_devices_permission)
        setToolbar("dk_perm_utils_app_diag_nearby_title")
        setStyle()
    }

    fun onRequestPermissionClicked(view: View) {
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
                    R.string.dk_common_app_diag_nearby_ko,
                    this@NearbyDevicesPermissionActivity::checkRequiredPermissions
                )
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                button_request_nearby_devices_permission.text =
                    DKResource.convertToString(applicationContext, "dk_perm_utils_permissions_text_button_nearby_devices_settings")
                handlePermissionTotallyDeclined(
                    this@NearbyDevicesPermissionActivity,
                    R.string.dk_common_app_diag_nearby_ko
                )
            }
        }

        request(
            this,
            permissionCallback as OnPermissionCallback,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            checkRequiredPermissions()
        }
    }

    private fun setStyle() {
        text_view_nearby_devices_permission_title.highlightMedium()
        text_view_nearby_devices_permission_text1.normalText()
        text_view_nearby_devices_permission_text2.normalText()
        button_request_nearby_devices_permission.button()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
