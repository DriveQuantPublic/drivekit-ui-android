package com.drivequant.drivekit.permissionsutils.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.drivequant.drivekit.permissionsutils.PermissionUtilsUI
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.permissions.BasePermissionActivity.Companion.PERMISSION_VIEWS_LIST_EXTRA

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

enum class PermissionView {
    ACTIVITY, LOCATION, BACKGROUND_TASK;

    fun launchActivity(activity: Activity, permissionViews: ArrayList<PermissionView>) {
        if (!this.isAuthorized(activity)) {
            activity.startActivity(this.buildIntent(activity, permissionViews))
        } else {
            permissionViews.remove(this)
            if (permissionViews.isEmpty()) {
                PermissionUtilsUI.permissionViewListener?.onFinish()
            } else {
                permissionViews.first().launchActivity(activity, permissionViews)
            }
        }
    }

    private fun isAuthorized(context: Activity): Boolean {
        return when (this) {
            LOCATION -> DiagnosisHelper.isLocationAuthorize(context)
            ACTIVITY -> DiagnosisHelper.isActivityRecognitionAuthorize(context)
            BACKGROUND_TASK -> DiagnosisHelper.isIgnoringBatteryOptimizations(context)
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
