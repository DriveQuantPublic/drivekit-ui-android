package com.drivequant.drivekit.permissionsutils.permissions

import android.content.Context

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

enum class PermissionView {
    ACTIVITY, LOCATION, BATTERY;

    fun launchActivity(context: Context, permissionViews:ArrayList<PermissionView>) {

    }
}