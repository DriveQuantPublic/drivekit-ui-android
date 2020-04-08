package com.drivequant.drivekit.permissionsutils.permissions

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.PERMISSION_ACTIVITY_RECOGNITION
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
import com.drivequant.drivekit.permissionsutils.diagnosis.OnPermissionCallback

class ActivityRecognitionPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognition_permission)
        findViewById<TextView>(R.id.text_view_activity_permission_title).headLine1()
        findViewById<TextView>(R.id.text_view_activity_permission_text).normalText()
        findViewById<Button>(R.id.button_request_activity_permission).button()
    }

    fun onRequestPermissionClicked(view: View) {
        checkRequiredPermissions()
    }

    private fun checkRequiredPermissions() {
        permissionCallback = object : OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                next()
                finish()
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
}
