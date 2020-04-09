package com.drivequant.drivekit.permissionsutils.permissions

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.PERMISSION_ACTIVITY_RECOGNITION
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
import com.drivequant.drivekit.permissionsutils.diagnosis.OnPermissionCallback
import kotlinx.android.synthetic.main.activity_recognition_permission.*

class ActivityRecognitionPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognition_permission)
        setStyle()
    }

    fun onRequestPermissionClicked(view: View) {
        checkRequiredPermissions()
    }

    private fun checkRequiredPermissions() {
        permissionCallback = object : OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                finish()
                next()
            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
                DKAlertDialog.AlertBuilder()
                    .init(this@ActivityRecognitionPermissionActivity)
                    .title(getString(R.string.dk_perm_utils_permissions))
                    .message(getString(R.string.dk_perm_utils_app_diag_activity_ko))
                    .cancelable(false)
                    .positiveButton(getString(R.string.dk_common_ok),
                        DialogInterface.OnClickListener { _, _ ->
                            checkRequiredPermissions()
                        })
                    .show()
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                button_request_activity_permission.text = getString(R.string.dk_perm_utils_open_settings)
                DKAlertDialog.AlertBuilder()
                    .init(this@ActivityRecognitionPermissionActivity)
                    .title(getString(R.string.dk_perm_utils_permissions))
                    .message(getString(R.string.dk_perm_utils_app_diag_activity_ko))
                    .cancelable(false)
                    .positiveButton(getString(R.string.dk_perm_utils_settings),
                        DialogInterface.OnClickListener { _, _ ->
                            startActivityForResult(
                                launchSettings(),
                                REQUEST_PERMISSIONS_OPEN_SETTINGS
                            )
                        })
                    .negativeButton(getString(R.string.dk_common_close),
                        DialogInterface.OnClickListener { dialog, _ ->
                            dialog.cancel()
                        })
                    .show()
            }
        }
        request(this, permissionCallback as OnPermissionCallback, PERMISSION_ACTIVITY_RECOGNITION)
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
