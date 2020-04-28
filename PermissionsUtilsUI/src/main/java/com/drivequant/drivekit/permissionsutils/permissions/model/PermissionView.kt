package com.drivequant.drivekit.permissionsutils.permissions.model

import android.app.Activity
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

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

enum class PermissionView {
    ACTIVITY, LOCATION, BACKGROUND_TASK;

    fun launchActivity(activity: Activity, permissionViews: ArrayList<PermissionView>) {
        when (getCurrentPermissionStatus(activity)) {
            PermissionStatus.NOT_VALID -> {
                activity.startActivity(this.buildIntent(activity, permissionViews))
            }
            PermissionStatus.VALID -> {
                permissionViews.remove(this)
                if (permissionViews.isEmpty()) {
                    PermissionsUtilsUI.permissionViewListener?.onFinish()
                } else {
                    permissionViews.first().launchActivity(activity, permissionViews)
                }
            }
        }
    }

    private fun getCurrentPermissionStatus(context: Activity): PermissionStatus {
        return when (this) {
            LOCATION -> DiagnosisHelper.getLocationStatus(context)
            ACTIVITY -> DiagnosisHelper.getActivityStatus(context)
            BACKGROUND_TASK -> DiagnosisHelper.getBatteryOptimizationsStatus(context)
        }
    }

    private fun buildIntent(context: Context, permissionViews: ArrayList<PermissionView>): Intent {
        val intent = when (this) {
            LOCATION -> Intent(context, LocationPermissionActivity::class.java)
            ACTIVITY -> Intent(context, ActivityRecognitionPermissionActivity::class.java)
            BACKGROUND_TASK -> Intent(context, BackgroundTaskPermissionActivity::class.java)
        }
        intent.putExtra(PERMISSION_VIEWS_LIST_EXTRA, permissionViews)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        return intent
    }
}
