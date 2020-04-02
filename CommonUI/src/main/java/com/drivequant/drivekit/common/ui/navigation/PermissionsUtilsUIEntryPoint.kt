package com.drivequant.drivekit.common.ui.navigation

import android.content.Context

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

interface PermissionsUtilsUIEntryPoint {
    fun startActivityPermissionActivity(context: Context)
    fun startLocationPermissionActivity(context: Context)
    fun startBatteryOptimizationPermissionActivity(context: Context)
}