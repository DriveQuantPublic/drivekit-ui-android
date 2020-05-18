package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
import kotlinx.android.synthetic.main.activity_recognition_permission.*

class ActivityRecognitionPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognition_permission)
        setToolbar()
        setStyle()
    }

    fun onRequestPermissionClicked(view: View) {
        checkRequiredPermissions()
    }

    private fun checkRequiredPermissions() {
        permissionCallback = object :
            OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                finish()
                next()
            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
               handlePermissionDeclined(this@ActivityRecognitionPermissionActivity,R.string.dk_perm_utils_app_diag_activity_ko,
                   this@ActivityRecognitionPermissionActivity::checkRequiredPermissions)
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                button_request_activity_permission.text = getString(R.string.dk_perm_utils_permissions_text_button_activity_settings)
                handlePermissionTotallyDeclined(this@ActivityRecognitionPermissionActivity, R.string.dk_perm_utils_app_diag_activity_ko)
            }
        }
        request(this, permissionCallback as OnPermissionCallback, Manifest.permission.ACTIVITY_RECOGNITION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            checkRequiredPermissions()
        }
    }

    private fun setStyle() {
        text_view_activity_permission_title.highlightMedium()
        text_view_activity_permission_text.normalText()
        button_request_activity_permission.button()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
