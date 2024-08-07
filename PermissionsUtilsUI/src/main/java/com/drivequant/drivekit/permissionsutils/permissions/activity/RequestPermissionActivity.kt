package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.drivequant.drivekit.common.ui.extension.headLine1WithColor
import com.drivequant.drivekit.common.ui.extension.normalTextWithColor
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.core.utils.getPackageInfoCompat
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback

open class RequestPermissionActivity : AppCompatActivity(),ActivityCompat.OnRequestPermissionsResultCallback {

    protected var permissionCallback: OnPermissionCallback? = null
    protected var alertDialog:AlertDialog? = null

    @Suppress("UNCHECKED_CAST")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == DiagnosisHelper.REQUEST_PERMISSIONS) {
            if (verifyPermissions(grantResults)) {
                permissionCallback?.onPermissionGranted(permissions as Array<String>)
            } else {
                val declinedPermissions = declinedPermissions(this, permissions as Array<String>)
                val deniedPermissionsLength = arrayListOf<Boolean>()
                for (permissionName in declinedPermissions) {
                    if (!isExplanationNeeded(this, permissionName)) {
                        permissionCallback?.onPermissionTotallyDeclined(permissionName)
                        deniedPermissionsLength.add(false)
                    }
                }
                if (deniedPermissionsLength.isEmpty()) {
                    permissionCallback?.onPermissionDeclined(declinedPermissions)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun request(context: Activity, vararg permissionName: String) {
        handlePermissions(context, permissionName as Array<String>)
    }

    private fun handlePermissions(context: Activity, permissionNames: Array<String>) {
        val permissions = declinedPermissionsAsList(context, permissionNames)
        if (permissions.isEmpty()) {
            permissionCallback?.onPermissionGranted(permissionNames)
            return
        }
        val hasAlertWindowPermission = permissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)
        if (hasAlertWindowPermission) {
            val index = permissions.indexOf(Manifest.permission.SYSTEM_ALERT_WINDOW)
            permissions.removeAt(index)
        }
        ActivityCompat.requestPermissions(context, permissions.toTypedArray(),
            DiagnosisHelper.REQUEST_PERMISSIONS
        )
    }

    private fun verifyPermissions(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) {
            return false
        }
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun declinedPermissions(context: Context, permissions: Array<String>): Array<String> {
        val permissionsNeeded = java.util.ArrayList<String>()
        for (permission in permissions) {
            if (isPermissionDeclined(context, permission) && permissionExists(context, permission)) {
                permissionsNeeded.add(permission)
            }
        }
        return permissionsNeeded.toTypedArray()
    }

    private fun isPermissionDeclined(context: Context, permission: String): Boolean = ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED

    protected fun isExplanationNeeded(activity: Activity, permissionName: String): Boolean = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName)

    private fun permissionExists(context: Context, permissionName: String): Boolean {
        try {
            val packageInfo = context.packageManager.getPackageInfoCompat(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            )
            return packageInfo.requestedPermissions?.contains(permissionName) ?: false
        } catch (e: Exception) {
            DriveKitLog.e(PermissionsUtilsUI.TAG, "Could not find permission: $permissionName")
        }

        return false
    }

    private fun declinedPermissionsAsList(context: Context, permissions: Array<String>): MutableList<String> {
        val permissionsNeeded = java.util.ArrayList<String>()
        for (permission in permissions) {
            if (isPermissionDeclined(context, permission) && permissionExists(context, permission)) {
                permissionsNeeded.add(permission)
            }
        }
        return permissionsNeeded
    }

    protected fun handlePermissionDeclined(context: Context, descriptionId: Int, callback: () -> Unit) {
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(context)
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(positiveListener = { _, _ -> callback() })
            .show()

        val titleTextView = alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
        val descriptionTextView = alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)

        titleTextView?.text = getString(com.drivequant.drivekit.common.ui.R.string.dk_common_permissions)
        descriptionTextView?.text = getString(descriptionId)

        titleTextView?.headLine1WithColor()
        descriptionTextView?.normalTextWithColor()
    }

    protected fun handlePermissionTotallyDeclined(context: Context, descriptionId: Int) {
        alertDialog = DKAlertDialog.LayoutBuilder()
            .init(context)
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(getString(R.string.dk_perm_utils_permissions_popup_button_settings)) { _, _ ->
                startActivityForResult(
                    DiagnosisHelper.buildSettingsIntent(context),
                    DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
                )
            }
            .negativeButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_close))
            .show()

        val titleTextView = alertDialog?.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
        val descriptionTextView = alertDialog?.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)

        titleTextView?.text = getString(com.drivequant.drivekit.common.ui.R.string.dk_common_permissions)
        descriptionTextView?.text = getString(descriptionId)

        titleTextView?.headLine1WithColor()
        descriptionTextView?.normalTextWithColor()
    }
}
