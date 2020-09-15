package com.drivequant.drivekit.permissionsutils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController.permissionsUtilsUIEntryPoint
import com.drivequant.drivekit.common.ui.navigation.PermissionsUtilsUIEntryPoint
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.activity.AppDiagnosisActivity
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionType
import com.drivequant.drivekit.permissionsutils.diagnosis.model.SensorType
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener
import com.drivequant.drivekit.common.ui.utils.ContactType
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

object PermissionsUtilsUI : PermissionsUtilsUIEntryPoint {

    internal var permissionViewListener: PermissionViewListener? = null
    internal var isBluetoothNeeded: Boolean = false
    internal var shouldDisplayDiagnosisLogs: Boolean = false
    internal var contactType: ContactType = ContactType.NONE
    internal var logPathFile: String = "/drivekit-permissions-utils/logs/"

    fun initialize() {
        permissionsUtilsUIEntryPoint = this
    }

    fun showPermissionViews(
        context: Context,
        permissionView: ArrayList<PermissionView>,
        permissionViewListener: PermissionViewListener
    ) {
        this.permissionViewListener = permissionViewListener
        permissionView.first().launchActivity(context, permissionView)
    }

    override fun startAppDiagnosisActivity(context: Context) =
        context.startActivity(Intent(context, AppDiagnosisActivity::class.java))

    fun configureBluetooth(isBluetoothNeeded: Boolean) {
        this.isBluetoothNeeded = isBluetoothNeeded
    }

    fun configureDiagnosisLogs(shouldDisplayDiagnosisLogs: Boolean) {
        this.shouldDisplayDiagnosisLogs = shouldDisplayDiagnosisLogs
    }

    fun configureContactType(ContactType: ContactType) {
        this.contactType = ContactType
    }

    fun configureLogPathFile(logPathFile: String) {
        this.logPathFile = logPathFile
    }

    fun hasError(activity: Activity): Boolean {
        val permissions = arrayListOf(
            PermissionType.LOCATION,
            PermissionType.ACTIVITY,
            PermissionType.NOTIFICATION
        )

        permissions.forEach {
            if (DiagnosisHelper.getPermissionStatus(activity, it) == PermissionStatus.NOT_VALID)
                return true
        }

        if (!DiagnosisHelper.isSensorActivated(
                activity,
                SensorType.BLUETOOTH
            ) && isBluetoothNeeded
        ) {
            return true
        }

        if (!DiagnosisHelper.isSensorActivated(activity, SensorType.GPS)) {
            return true
        }

        return !DiagnosisHelper.isNetworkReachable(activity)
    }

    fun getDiagnosisDescription(activity: Activity): String {
        val locationMail =
            when (DiagnosisHelper.getPermissionStatus(activity, PermissionType.LOCATION)) {
                PermissionStatus.VALID -> activity.getString(R.string.dk_common_yes)
                PermissionStatus.NOT_VALID -> activity.getString(R.string.dk_common_no)
            }

        val activityMail =
            when (DiagnosisHelper.getPermissionStatus(activity, PermissionType.ACTIVITY)) {
                PermissionStatus.VALID -> activity.getString(R.string.dk_common_yes)
                PermissionStatus.NOT_VALID -> activity.getString(R.string.dk_common_no)
            }

        val autoResetMail = when (DiagnosisHelper.getAutoResetStatus(activity)) {
            PermissionStatus.VALID -> activity.getString(R.string.dk_common_yes)
            PermissionStatus.NOT_VALID -> activity.getString(R.string.dk_common_no)
        }

        val notificationMail =
            when (DiagnosisHelper.getPermissionStatus(activity, PermissionType.NOTIFICATION)) {
                PermissionStatus.VALID -> activity.getString(R.string.dk_common_yes)
                PermissionStatus.NOT_VALID -> activity.getString(R.string.dk_common_no)
            }

        val batteryOptimization =
            when(DiagnosisHelper.getBatteryOptimizationsStatus(activity)) {
                PermissionStatus.VALID -> activity.getString(R.string.dk_common_yes)
                PermissionStatus.NOT_VALID -> activity.getString(R.string.dk_common_no)
            }

        val versionName = try {
            val pInfo = DriveKit.applicationContext!!.packageManager.getPackageInfo(DriveKit.applicationContext!!.packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "noName"
        }

        val gpsMail =
            if (DiagnosisHelper.isSensorActivated(activity, SensorType.GPS)) activity.getString(
                R.string.dk_common_yes
            ) else activity.getString(
                R.string.dk_common_no
            )

        val bluetoothMail =
            if (DiagnosisHelper.isSensorActivated(activity, SensorType.BLUETOOTH)) activity.getString(
                R.string.dk_common_yes
            ) else activity.getString(
                R.string.dk_common_no
            )
        val connectivityMail =
            if (DiagnosisHelper.isNetworkReachable(activity)) activity.getString(R.string.dk_common_yes) else activity.getString(
                R.string.dk_common_no
            )

        var mailBody =
            "${activity.getString(R.string.dk_perm_utils_app_diag_email_location)} $locationMail \n"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mailBody +=
                "${activity.getString(R.string.dk_perm_utils_app_diag_email_activity)} $activityMail \n"

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mailBody += "${activity.getString(R.string.dk_perm_utils_app_diag_email_auto_reset)} $autoResetMail \n"
        }

        mailBody += "${activity.getString(R.string.dk_perm_utils_app_diag_email_notification)} $notificationMail \n"
        mailBody += "${activity.getString(R.string.dk_perm_utils_app_diag_email_location_sensor)} $gpsMail \n"

        if (isBluetoothNeeded) {
            mailBody += "${activity.getString(R.string.dk_perm_utils_app_diag_email_bluetooth)} $bluetoothMail \n"
        }

        mailBody += "${activity.getString(R.string.dk_perm_utils_app_diag_email_network)}  $connectivityMail \n"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mailBody += "${activity.getString(R.string.dk_perm_utils_app_diag_email_battery)}  $batteryOptimization \n\n"
        }

        mailBody += "${activity.getString(R.string.dk_perm_utils_app_diag_email_model)}   ${Build.MANUFACTURER.toUpperCase(
            Locale.getDefault()
        )} ${Build.MODEL} \n"
        mailBody += "${activity.getString(R.string.dk_perm_utils_app_diag_email_os)} Android \n"
        mailBody += "${activity.getString(R.string.dk_perm_utils_app_diag_email_os_version)} ${Build.VERSION.RELEASE} \n"
        mailBody += "${activity.getString(R.string.dk_perm_utils_app_diag_email_app_version)} $versionName \n"
        return mailBody
    }
}