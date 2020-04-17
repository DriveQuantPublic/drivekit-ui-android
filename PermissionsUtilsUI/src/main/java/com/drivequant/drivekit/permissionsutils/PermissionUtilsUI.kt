package com.drivequant.drivekit.permissionsutils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController.permissionsUtilsUIEntryPoint
import com.drivequant.drivekit.common.ui.navigation.PermissionsUtilsUIEntryPoint
import com.drivequant.drivekit.permissionsutils.diagnosis.activity.AppDiagnosisActivity
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener
import com.drivequant.drivekit.permissionsutils.permissions.model.ContactType
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

object PermissionUtilsUI : PermissionsUtilsUIEntryPoint {

    internal var permissionViewListener: PermissionViewListener? = null
    internal var isBluetoothNeeded:Boolean = true
    internal var shouldDisplayDiagnosisLogs:Boolean = true
    internal var contactType:ContactType = ContactType.NONE

    fun initialize() {
        permissionsUtilsUIEntryPoint = this
    }

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

    fun configureBluetooth(isBluetoothNeeded: Boolean) {
        this.isBluetoothNeeded = isBluetoothNeeded
    }

    fun configureDiagnosisLogs(shouldDisplayDiagnosisLogs: Boolean) {
        this.shouldDisplayDiagnosisLogs = shouldDisplayDiagnosisLogs
    }

    fun configureContactType(contactType: ContactType) {
        this.contactType = contactType
    }

    fun hasError(): Boolean {
        return false
        //TODO
    }
}