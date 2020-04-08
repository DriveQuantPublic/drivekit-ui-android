package com.drivequant.drivekit.permissionsutils.permissions

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.PERMISSION_BACKGROUND_LOCATION
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.PERMISSION_LOCATION
import com.drivequant.drivekit.permissionsutils.diagnosis.OnPermissionCallback

class LocationPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_permission)

        findViewById<TextView>(R.id.text_view_permission_location_title).headLine1()
        findViewById<TextView>(R.id.text_view_location_permission_text1).normalText()
        findViewById<Button>(R.id.button_request_location_permission).button()
        val textViewLocationPermissionText2 = findViewById<TextView>(R.id.text_view_location_permission_text2)

        textViewLocationPermissionText2.normalText()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textViewLocationPermissionText2.text =
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
                DKAlertDialog.AlertBuilder()
                    .init(this@LocationPermissionActivity)
                    .title(getString(R.string.dk_perm_utils_permissions))
                    .message(getString(R.string.dk_perm_utils_app_diag_location_ko_android))
                    .cancelable(false)
                    .positiveButton(getString(R.string.dk_common_ok),
                        DialogInterface.OnClickListener { _, _ ->
                            checkRequiredPermissions()
                        })
                    .show()
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                DKAlertDialog.AlertBuilder()
                    .init(this@LocationPermissionActivity)
                    .title(getString(R.string.dk_perm_utils_permissions))
                    .message(getString(R.string.dk_perm_utils_app_diag_location_ko_android))
                    .cancelable(false)
                    .positiveButton(getString(R.string.dk_perm_utils_settings),
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

    private fun forward() {
        finish()
        next()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            checkRequiredPermissions()
        }
    }
}
