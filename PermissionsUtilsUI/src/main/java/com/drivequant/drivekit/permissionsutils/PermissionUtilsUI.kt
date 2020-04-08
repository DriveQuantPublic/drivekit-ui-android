package com.drivequant.drivekit.permissionsutils

import android.app.Activity
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController.permissionsUtilsUIEntryPoint
import com.drivequant.drivekit.common.ui.navigation.PermissionsUtilsUIEntryPoint
import com.drivequant.drivekit.permissionsutils.permissions.*

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
        activity: Activity,
        permissionView: ArrayList<PermissionView>,
        permissionViewListener: PermissionViewListener) {
        this.permissionViewListener = permissionViewListener
        permissionView.first().launchActivity(activity,permissionView)
    }

    override fun startActivityPermissionActivity(activity: Activity) = PermissionView.ACTIVITY.launchActivity(activity,arrayListOf(PermissionView.ACTIVITY))

    override fun startLocationPermissionActivity(activity: Activity) = PermissionView.LOCATION.launchActivity(activity,arrayListOf(PermissionView.LOCATION))

    override fun startBatteryOptimizationPermissionActivity(activity: Activity) = PermissionView.BACKGROUND_TASK.launchActivity(activity,arrayListOf(PermissionView.BACKGROUND_TASK))
}