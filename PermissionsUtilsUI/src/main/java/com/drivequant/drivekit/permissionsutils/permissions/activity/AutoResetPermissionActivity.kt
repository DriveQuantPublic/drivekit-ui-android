package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import kotlinx.android.synthetic.main.activity_auto_reset_permission.*

class AutoResetPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_reset_permission)
        setToolbar("dk_perm_utils_permissions_auto_reset_title")
        setStyle()
    }

    fun onRequestPermissionClicked(view: View) {
        checkRequiredPermissions()
    }

    private fun checkRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (DiagnosisHelper.getAutoResetStatus(this) != PermissionStatus.VALID) {
                val intent = DiagnosisHelper.buildSettingsIntent(this)
                intent.action = Intent.ACTION_AUTO_REVOKE_PERMISSIONS
                startActivityForResult(intent, DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS)
            } else {
                forward()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            if (DiagnosisHelper.getAutoResetStatus(this) != PermissionStatus.VALID){
                handlePermissionDeclined(
                    this@AutoResetPermissionActivity,
                    R.string.dk_perm_utils_app_diag_auto_reset_ko,
                    this@AutoResetPermissionActivity::checkRequiredPermissions
                )
            } else {
                forward()
            }
        }
    }

    private fun setStyle() {
        text_view_auto_reset_permission_title.highlightMedium()
        text_view_auto_reset_permission_text1.normalText()
        text_view_auto_reset_permission_text2.normalText()
        button_request_auto_reset_permission.button()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
