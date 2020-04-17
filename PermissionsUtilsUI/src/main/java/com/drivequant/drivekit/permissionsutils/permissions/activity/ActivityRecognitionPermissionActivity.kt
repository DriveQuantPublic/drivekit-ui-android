package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
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
        permissionCallback = object :
            OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                finish()
                next()
            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
                val activityDialog = DKAlertDialog.LayoutBuilder()
                    .init(this@ActivityRecognitionPermissionActivity)
                    .layout(R.layout.template_alert_dialog_layout)
                    .cancelable(false)
                    .positiveButton(getString(R.string.dk_common_ok),
                        DialogInterface.OnClickListener { _, _ ->
                            checkRequiredPermissions()
                        })
                    .show()

                val titleTextView = activityDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView = activityDialog.findViewById<TextView>(R.id.text_view_alert_description)

                titleTextView?.text = getString(R.string.dk_common_permissions)
                descriptionTextView?.text = getString(R.string.dk_perm_utils_app_diag_activity_ko)

                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                button_request_activity_permission.text = getString(R.string.dk_perm_utils_permissions_text_button_activity_settings)
                val activityDialog = DKAlertDialog.LayoutBuilder()
                    .init(this@ActivityRecognitionPermissionActivity)
                    .layout(R.layout.template_alert_dialog_layout)
                    .cancelable(false)
                    .positiveButton(getString(R.string.dk_perm_utils_permissions_popup_button_settings),
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

                val titleTextView = activityDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView = activityDialog.findViewById<TextView>(R.id.text_view_alert_description)

                titleTextView?.text = getString(R.string.dk_common_permissions)
                descriptionTextView?.text = getString(R.string.dk_perm_utils_app_diag_activity_ko)

                titleTextView?.headLine1()
                descriptionTextView?.normalText()
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
