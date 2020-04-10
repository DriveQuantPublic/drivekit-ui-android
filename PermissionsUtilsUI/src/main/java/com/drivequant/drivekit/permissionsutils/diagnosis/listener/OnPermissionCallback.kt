package com.drivequant.drivekit.permissionsutils.diagnosis.listener

/**
 * Created by Mohamed on 2020-04-03.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

interface OnPermissionCallback {
    fun onPermissionGranted(permissionName: Array<String>)
    fun onPermissionDeclined(permissionName: Array<String>)
    fun onPermissionTotallyDeclined(permissionName: String)
}