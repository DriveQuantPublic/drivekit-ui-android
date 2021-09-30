package com.drivequant.drivekit.permissionsutils.permissions.model

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import com.drivequant.drivekit.permissionsutils.permissions.activity.ActivityRecognitionPermissionActivity
import com.drivequant.drivekit.permissionsutils.permissions.activity.BackgroundTaskPermissionActivity
import com.drivequant.drivekit.permissionsutils.permissions.activity.BasePermissionActivity.Companion.PERMISSION_VIEWS_LIST_EXTRA
import com.drivequant.drivekit.permissionsutils.permissions.activity.LocationPermissionActivity
import com.drivequant.drivekit.permissionsutils.permissions.activity.NearbyDevicesPermissionActivity

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

enum class PermissionView {
    ACTIVITY, LOCATION, BACKGROUND_TASK, NEARBY_DEVICES;

    fun launchActivity(context: Context, permissionViews: ArrayList<PermissionView>) {
        when (getCurrentPermissionStatus(context)) {
            PermissionStatus.NOT_VALID -> {
                context.startActivity(this.buildIntent(context, permissionViews))
            }
            PermissionStatus.VALID -> {
                permissionViews.remove(this)
                if (permissionViews.isEmpty()) {
                    PermissionsUtilsUI.permissionViewListener?.onFinish()
                } else {
                    permissionViews.first().launchActivity(context, permissionViews)
                }
            }
        }
    }

    private fun getCurrentPermissionStatus(context: Context): PermissionStatus {
        return when (this) {
            LOCATION -> DiagnosisHelper.getLocationStatus(context)
            ACTIVITY -> DiagnosisHelper.getActivityStatus(context)
            BACKGROUND_TASK -> DiagnosisHelper.getBatteryOptimizationsStatus(context)
            NEARBY_DEVICES -> DiagnosisHelper.getNearbyDevicesStatus(context)
        }
    }

    private fun buildIntent(context: Context, permissionViews: ArrayList<PermissionView>): Intent {
        val selectedClass = when (this) {
            LOCATION -> LocationPermissionActivity::class.java
            ACTIVITY -> ActivityRecognitionPermissionActivity::class.java
            BACKGROUND_TASK -> BackgroundTaskPermissionActivity::class.java
            NEARBY_DEVICES -> NearbyDevicesPermissionActivity::class.java
        }
        val intent = Intent(context, selectedClass)
        intent.putExtra(PERMISSION_VIEWS_LIST_EXTRA, permissionViews)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        return intent
    }
}
