package com.drivequant.drivekit.common.ui.navigation

import android.app.Activity

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

interface PermissionsUtilsUIEntryPoint {
    fun startActivityPermissionActivity(activity: Activity)
    fun startLocationPermissionActivity(activity: Activity)
    fun startBatteryOptimizationPermissionActivity(activity: Activity)
}