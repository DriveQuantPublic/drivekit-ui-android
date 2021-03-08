package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback

open class RequestPermissionActivity : AppCompatActivity(),ActivityCompat.OnRequestPermissionsResultCallback {

    protected var permissionCallback: OnPermissionCallback? = null
    protected var alertDialog:AlertDialog? = null

    @Suppress("UNCHECKED_CAST")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
    protected fun request(context: Activity, permissionCallback: OnPermissionCallback, vararg permissionName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            handlePermissions(context, permissionName as Array<String>)
        } else {
            permissionCallback.onPermissionGranted(permissionName as Array<String>)
        }
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
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            )
            if (packageInfo.requestedPermissions != null) {
                for (permission in packageInfo.requestedPermissions) {
                    if (permission == permissionName) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("DriveQuant Permission", "Could not find permission: $permissionName")
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
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(positiveListener =
                DialogInterface.OnClickListener { _, _ ->
                    callback()
                })
            .show()

        val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_description)

        titleTextView?.text = getString(R.string.dk_common_permissions)
        descriptionTextView?.text = getString(descriptionId)

        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }

    protected fun handlePermissionTotallyDeclined(context: Context, descriptionId: Int) {
        alertDialog = DKAlertDialog.LayoutBuilder()
            .init(context)
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(getString(R.string.dk_perm_utils_permissions_popup_button_settings),
                DialogInterface.OnClickListener { _, _ ->
                    startActivityForResult(
                        DiagnosisHelper.buildSettingsIntent(context),
                        DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
                    )
                })
            .negativeButton(getString(R.string.dk_common_close))
            .show()

        val titleTextView = alertDialog?.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = alertDialog?.findViewById<TextView>(R.id.text_view_alert_description)

        titleTextView?.text = getString(R.string.dk_common_permissions)
        descriptionTextView?.text = getString(descriptionId)

        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }
}