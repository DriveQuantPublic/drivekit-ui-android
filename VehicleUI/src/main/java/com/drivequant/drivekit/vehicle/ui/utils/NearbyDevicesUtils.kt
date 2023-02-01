package com.drivequant.drivekit.vehicle.ui.utils

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.vehicle.ui.R

internal object NearbyDevicesUtils {

    const val NEARBY_DEVICES_PERMISSIONS_REQUEST_CODE = 100

    fun isBluetoothScanAuthorized(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                DriveKit.applicationContext, Manifest.permission.BLUETOOTH_SCAN) == PermissionChecker.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                DriveKit.applicationContext, Manifest.permission.BLUETOOTH_CONNECT) == PermissionChecker.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @JvmOverloads
    fun displayPermissionsError(activity: Activity, redirectToSettings: Boolean = false) {
        lateinit var buttonTextId: String
        lateinit var descriptionId: String
        if (redirectToSettings) {
            descriptionId = "dk_common_app_diag_nearby_ko"
            buttonTextId = "dk_common_settings"
        } else {
            descriptionId = "dk_common_app_diag_nearby_ko"
            buttonTextId = "dk_common_app_diag_nearby_link"
        }
        val alert = DKAlertDialog.LayoutBuilder()
            .init(activity)
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(true)
            .positiveButton(DKResource.convertToString(
                activity,
                buttonTextId
            ), positiveListener = { dialogInterface: DialogInterface?, _: Int ->
                run {
                    dialogInterface?.dismiss()
                    if (redirectToSettings) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", activity.packageName, null)
                        intent.data = uri
                        startActivityForResult(
                            activity,
                            intent,
                            NEARBY_DEVICES_PERMISSIONS_REQUEST_CODE,
                            null
                        )
                    } else {
                        requestPermissions(activity)
                    }
                }
            })
            .show()

        val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val description = alert.findViewById<TextView>(R.id.text_view_alert_description)

        title?.text = DKResource.buildString(
            activity,
            DriveKitUI.colors.mainFontColor(),
            DriveKitUI.colors.mainFontColor(),
            "dk_common_app_diag_nearby"
        )
        title?.headLine1()
        description?.text = DKResource.convertToString(activity, descriptionId)
        description?.normalText()
    }

    private fun requestPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), NEARBY_DEVICES_PERMISSIONS_REQUEST_CODE)
        }
    }
}
