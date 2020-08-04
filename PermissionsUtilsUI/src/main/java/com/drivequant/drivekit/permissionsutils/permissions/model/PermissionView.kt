package com.drivequant.drivekit.permissionsutils.permissions.model

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import com.drivequant.drivekit.permissionsutils.permissions.activity.ActivityRecognitionPermissionActivity
import com.drivequant.drivekit.permissionsutils.permissions.activity.AutoResetPermissionActivity
import com.drivequant.drivekit.permissionsutils.permissions.activity.BackgroundTaskPermissionActivity
import com.drivequant.drivekit.permissionsutils.permissions.activity.BasePermissionActivity.Companion.PERMISSION_VIEWS_LIST_EXTRA
import com.drivequant.drivekit.permissionsutils.permissions.activity.LocationPermissionActivity

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

enum class PermissionView {
    ACTIVITY, LOCATION, AUTO_RESET, BACKGROUND_TASK;

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

    private fun getCurrentPermissionStatus(activity: Activity): PermissionStatus {
        return when (this) {
            LOCATION -> DiagnosisHelper.getLocationStatus(activity)
            ACTIVITY -> DiagnosisHelper.getActivityStatus(activity)
            AUTO_RESET -> DiagnosisHelper.getAutoResetStatus(activity)
            BACKGROUND_TASK -> DiagnosisHelper.getBatteryOptimizationsStatus(activity)
        }
    }

    private fun buildIntent(activity: Activity, permissionViews: ArrayList<PermissionView>): Intent {
        val intent = when (this) {
            LOCATION -> Intent(activity, LocationPermissionActivity::class.java)
            ACTIVITY -> Intent(activity, ActivityRecognitionPermissionActivity::class.java)
            AUTO_RESET -> Intent(activity, AutoResetPermissionActivity::class.java)
            BACKGROUND_TASK -> Intent(activity, BackgroundTaskPermissionActivity::class.java)
        }
        intent.putExtra(PERMISSION_VIEWS_LIST_EXTRA, permissionViews)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        return intent
    }
}
