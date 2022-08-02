package com.drivequant.drivekit.permissionsutils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController.permissionsUtilsUIEntryPoint
import com.drivequant.drivekit.common.ui.navigation.PermissionsUtilsUIEntryPoint
import com.drivequant.drivekit.permissionsutils.diagnosis.activity.AppDiagnosisActivity
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener
import com.drivequant.drivekit.common.ui.utils.ContactType
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.utils.ConnectivityType
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.core.utils.PermissionStatus
import com.drivequant.drivekit.core.utils.PermissionType
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

object PermissionsUtilsUI : PermissionsUtilsUIEntryPoint {
    internal const val TAG = "DriveKit PermissionsUtils UI"
    internal var permissionViewListener: PermissionViewListener? = null
    internal var isBluetoothNeeded: Boolean = false
    internal var contactType: ContactType = ContactType.NONE
    internal var tutorialUrl:String ? = null

    fun initialize() {
        permissionsUtilsUIEntryPoint = this
    }

    @Deprecated("The method is deprecated: permissionView parameter is now ignored",
        ReplaceWith(
        "PermissionsUtilsUI.showPermissionViews(context, permissionViewListener)",
        "com.drivequant.drivekit.permissionsutils")
    )
    fun showPermissionViews(
        context: Context,
        permissionView: ArrayList<PermissionView>,
        permissionViewListener: PermissionViewListener
    ) {
        showPermissionViews(context, permissionViewListener)
    }

    fun showPermissionViews(
        context: Context,
        permissionViewListener: PermissionViewListener
    ) {
        this.permissionViewListener = permissionViewListener
        val permissions = ArrayList<PermissionView>()
        permissions.addAll(
            listOf(
                PermissionView.NOTIFICATIONS,
                PermissionView.LOCATION,
                PermissionView.ACTIVITY,
                PermissionView.BACKGROUND_TASK,
                PermissionView.NEARBY_DEVICES
            )
        )
        permissions.first().launchActivity(context, permissions)
    }

    override fun startAppDiagnosisActivity(context: Context) =
        context.startActivity(Intent(context, AppDiagnosisActivity::class.java))

    fun configureBluetooth(isBluetoothNeeded: Boolean) {
        this.isBluetoothNeeded = isBluetoothNeeded
    }

    @Deprecated("Logs are now enabled by default. To disable logging, just call DriveKit.disableLogging()")
    fun configureDiagnosisLogs(shouldDisplayDiagnosisLogs: Boolean) { }

    fun configureContactType(ContactType: ContactType) {
        this.contactType = ContactType
    }

    @Deprecated("Logs are now only driven by DriveKit Core module.")
    fun configureLogPathFile(logPathFile: String) { }

    fun configureTutorialUrl(tutorialUrl: String) {
        this.tutorialUrl = tutorialUrl
    }

    fun hasError(context: Context): Boolean {
        PermissionType.values().forEach {
            if (DiagnosisHelper.getPermissionStatus(context, it) == PermissionStatus.NOT_VALID)
                return true
        }

        if (!DiagnosisHelper.isActivated(context, ConnectivityType.BLUETOOTH) && isBluetoothNeeded) {
            return true
        }

        if (!DiagnosisHelper.isActivated(context, ConnectivityType.GPS)) {
            return true
        }

        if (DiagnosisHelper.getBatteryOptimizationsStatus(context) == PermissionStatus.NOT_VALID
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        ) {
            return true
        }

        return !DiagnosisHelper.isNetworkReachable(context)
    }

    fun getDiagnosisDescription(context: Context): String {
        val locationMail =
            when (DiagnosisHelper.getPermissionStatus(context, PermissionType.LOCATION)) {
                PermissionStatus.VALID -> context.getString(R.string.dk_common_yes)
                PermissionStatus.NOT_VALID -> context.getString(R.string.dk_common_no)
            }

        val activityMail =
            when (DiagnosisHelper.getPermissionStatus(context, PermissionType.ACTIVITY)) {
                PermissionStatus.VALID -> context.getString(R.string.dk_common_yes)
                PermissionStatus.NOT_VALID -> context.getString(R.string.dk_common_no)
            }

        val autoResetMail = when (DiagnosisHelper.getAutoResetStatus(context)) {
            PermissionStatus.VALID -> context.getString(R.string.dk_common_yes)
            PermissionStatus.NOT_VALID -> context.getString(R.string.dk_common_no)
        }

        val notificationMail =
            when (DiagnosisHelper.getPermissionStatus(context, PermissionType.NOTIFICATION)) {
                PermissionStatus.VALID -> context.getString(R.string.dk_common_yes)
                PermissionStatus.NOT_VALID -> context.getString(R.string.dk_common_no)
            }

        val batteryOptimization = when (DiagnosisHelper.getBatteryOptimizationsStatus(context)) {
            PermissionStatus.VALID -> context.getString(R.string.dk_perm_utils_app_diag_email_battery_disabled)
            PermissionStatus.NOT_VALID -> context.getString(R.string.dk_perm_utils_app_diag_email_battery_enabled)
        }

        val nearbyDevices = when (DiagnosisHelper.getNearbyDevicesStatus(context)) {
            PermissionStatus.VALID -> context.getString(R.string.dk_common_yes)
            PermissionStatus.NOT_VALID -> context.getString(R.string.dk_common_no)
        }

        val versionName = try {
            val pInfo = DriveKit.applicationContext!!.packageManager.getPackageInfo(DriveKit.applicationContext!!.packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "noName"
        }

        val gpsMail = if (DiagnosisHelper.isActivated(context, ConnectivityType.GPS)) context.getString(
                R.string.dk_common_yes
            ) else context.getString(
                R.string.dk_common_no
            )

        val bluetoothMail = if (DiagnosisHelper.isActivated(context, ConnectivityType.BLUETOOTH)) context.getString(
                R.string.dk_common_yes
            ) else context.getString(
                R.string.dk_common_no
            )
        val connectivityMail = if (DiagnosisHelper.isNetworkReachable(context)) context.getString(R.string.dk_common_yes) else context.getString(
                R.string.dk_common_no
            )

        var mailBody = "${context.getString(R.string.dk_perm_utils_app_diag_email_location)} $locationMail \n"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_activity)} $activityMail \n"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_auto_reset)} $autoResetMail \n"
        }

        mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_notification)} $notificationMail \n"
        mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_location_sensor)} $gpsMail \n"

        if (isBluetoothNeeded) {
            mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_bluetooth)} $bluetoothMail \n"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_nearby_title)} $nearbyDevices \n"
        }

        mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_network)} $connectivityMail \n"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_battery)} $batteryOptimization \n\n"
        }

        mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_model)} ${Build.MANUFACTURER.uppercase(Locale.getDefault())} ${Build.MODEL}\n"
        mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_os)} Android \n"
        mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_os_version)} ${Build.VERSION.RELEASE} \n"
        mailBody += "${context.getString(R.string.dk_perm_utils_app_diag_email_app_version)} $versionName \n"
        return mailBody
    }
}