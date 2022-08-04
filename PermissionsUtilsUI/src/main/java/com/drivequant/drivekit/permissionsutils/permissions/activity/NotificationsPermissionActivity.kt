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
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView
import kotlinx.android.synthetic.main.activity_notifications_permission.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class NotificationsPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_permission)
        setToolbar("dk_perm_utils_app_diag_notification_title")
        setStyle()
        manageSkipButton()
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
                    this@NotificationsPermissionActivity,
                    R.string.dk_perm_utils_app_diag_notification_ko,
                    this@NotificationsPermissionActivity::checkRequiredPermissions
                )
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                button_request_notifications_permission.text =
                    DKResource.convertToString(applicationContext, "dk_perm_utils_permissions_text_button_notifications_settings")
                handlePermissionTotallyDeclined(
                    this@NotificationsPermissionActivity,
                    R.string.dk_perm_utils_app_diag_notification_ko
                )
            }
        }

        request(
            this,
            permissionCallback as OnPermissionCallback,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    private fun manageSkipButton() {
        button_skip.setOnClickListener {
            skip(PermissionView.NOTIFICATIONS)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            checkRequiredPermissions()
        }
    }

    private fun setStyle() {
        text_view_notifications_permission_title.highlightMedium()
        text_view_notifications_permission_text1.normalText()
        text_view_notifications_permission_text2.normalText()
        button_request_notifications_permission.button()
        button_skip.normalText(DriveKitUI.colors.secondaryColor())
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
