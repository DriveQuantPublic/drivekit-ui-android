package com.drivequant.drivekit.permissionsutils

import android.content.Context
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController.permissionsUtilsUIEntryPoint
import com.drivequant.drivekit.common.ui.navigation.PermissionsUtilsUIEntryPoint
import com.drivequant.drivekit.permissionsutils.permissions.PermissionView
import com.drivequant.drivekit.permissionsutils.permissions.PermissionViewListener

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

object PermissionUtilsUI : PermissionsUtilsUIEntryPoint {

    internal var permissionViewListener: PermissionViewListener? = null

    fun initialize() {
        permissionsUtilsUIEntryPoint = this
    }

    fun showPermissionViews(
        context: Context,
        permissionView: ArrayList<PermissionView>,
        permissionViewListener: PermissionViewListener) {
        this.permissionViewListener = permissionViewListener
    }


    override fun startActivityPermissionActivity(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startLocationPermissionActivity(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startBatteryOptimizationPermissionActivity(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}