package com.drivequant.drivekit.permissionsutils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController.permissionsUtilsUIEntryPoint
import com.drivequant.drivekit.common.ui.navigation.PermissionsUtilsUIEntryPoint
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.activity.AppDiagnosisActivity
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionType
import com.drivequant.drivekit.permissionsutils.diagnosis.model.SensorType
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener
import com.drivequant.drivekit.common.ui.utils.ContactType
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

object PermissionUtilsUI : PermissionsUtilsUIEntryPoint {

    internal var permissionViewListener: PermissionViewListener? = null
    internal var isBluetoothNeeded: Boolean = true
    internal var shouldDisplayDiagnosisLogs: Boolean = true
    internal var contactType: ContactType = ContactType.NONE
    internal var logPathFile: String = "/drivekit-permissions-utils/logs/"

    @JvmStatic
    fun initialize() {
        permissionsUtilsUIEntryPoint = this
    }

    @JvmStatic
    fun showPermissionViews(
        activity: Activity,
        permissionView: ArrayList<PermissionView>,
        permissionViewListener: PermissionViewListener
    ) {
        this.permissionViewListener = permissionViewListener
        permissionView.first().launchActivity(activity, permissionView)
    }

    override fun startAppDiagnosisActivity(context: Context) =
        context.startActivity(Intent(context, AppDiagnosisActivity::class.java))

    @JvmStatic
    fun configureBluetooth(isBluetoothNeeded: Boolean) {
        this.isBluetoothNeeded = isBluetoothNeeded
    }

    @JvmStatic
    fun configureDiagnosisLogs(shouldDisplayDiagnosisLogs: Boolean) {
        this.shouldDisplayDiagnosisLogs = shouldDisplayDiagnosisLogs
    }

    @JvmStatic
    fun configureContactType(ContactType: ContactType) {
        this.contactType = ContactType
    }

    @JvmStatic
    fun configureLogPathFile(logPathFile: String) {
        this.logPathFile = logPathFile
    }

    @JvmStatic
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
}