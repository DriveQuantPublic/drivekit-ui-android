package com.drivequant.drivekit.permissionsutils.permissions

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
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
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.PERMISSION_BACKGROUND_LOCATION
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.PERMISSION_LOCATION
import com.drivequant.drivekit.permissionsutils.diagnosis.OnPermissionCallback
import kotlinx.android.synthetic.main.activity_location_permission.*

class LocationPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_permission)
        setStyle()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            text_view_location_permission_text2.text =
                getString(R.string.dk_perm_utils_permissions_location_text2_post_android10)
        }
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
               val locationDialog = DKAlertDialog.LayoutBuilder()
                    .init(this@LocationPermissionActivity)
                    .layout(R.layout.template_alert_dialog_layout)
                    .cancelable(false)
                    .positiveButton(getString(R.string.dk_common_ok),
                        DialogInterface.OnClickListener { _, _ ->
                            checkRequiredPermissions()
                        })
                    .show()

                val titleTextView = locationDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView = locationDialog.findViewById<TextView>(R.id.text_view_alert_description)

                titleTextView?.text = getString(R.string.dk_common_permissions)
                descriptionTextView?.text = getString(R.string.dk_perm_utils_app_diag_location_ko_android)

                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                button_request_location_permission.text = getString(R.string.dk_perm_utils_permissions_text_button_location_settings)
                val locationDialog = DKAlertDialog.LayoutBuilder()
                    .init(this@LocationPermissionActivity)
                    .layout(R.layout.template_alert_dialog_layout)
                    .message(getString(R.string.dk_perm_utils_app_diag_location_ko_android))
                    .cancelable(false)
                    .positiveButton(getString(R.string.dk_perm_utils_permissions_popup_button_settings),
                        DialogInterface.OnClickListener { _, _ ->
                            startActivityForResult(
                                launchSettings(),
                                DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
                            )
                        })
                    .negativeButton(getString(R.string.dk_common_close),
                        DialogInterface.OnClickListener { dialog, _ ->
                            dialog.cancel()
                        })
                    .show()

                val titleTextView = locationDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView = locationDialog.findViewById<TextView>(R.id.text_view_alert_description)

                titleTextView?.text = getString(R.string.dk_common_permissions)
                descriptionTextView?.text = getString(R.string.dk_perm_utils_app_diag_location_ko_android)

                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }
        }

        if (DiagnosisHelper.hasFineLocationPermission(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (DiagnosisHelper.hasBackgroundLocationApproved(this)) {
                    forward()
                } else {
                    request(this, permissionCallback as OnPermissionCallback,PERMISSION_BACKGROUND_LOCATION)
                }
            } else {
                forward()
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                request(this, permissionCallback as OnPermissionCallback, PERMISSION_LOCATION, PERMISSION_BACKGROUND_LOCATION)
            } else {
                request(this, permissionCallback as OnPermissionCallback, PERMISSION_LOCATION)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            checkRequiredPermissions()
        }
    }

    private fun forward() {
        finish()
        next()
    }

    private fun setStyle() {
        text_view_permission_location_title.highlightMedium()
        text_view_location_permission_text1.normalText()
        text_view_location_permission_text2.normalText()
        button_request_location_permission.button()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
