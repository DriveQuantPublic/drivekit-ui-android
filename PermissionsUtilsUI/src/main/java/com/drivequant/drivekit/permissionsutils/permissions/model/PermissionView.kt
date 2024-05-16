package com.drivequant.drivekit.permissionsutils.permissions.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.drivequant.drivekit.core.DriveKitSharedPreferencesUtils
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.core.utils.PermissionStatus
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.permissions.activity.*
import com.drivequant.drivekit.permissionsutils.permissions.activity.BasePermissionActivity.Companion.PERMISSION_VIEWS_LIST_EXTRA

/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

enum class PermissionView {
    ACTIVITY, LOCATION, BACKGROUND_TASK, NEARBY_DEVICES, NOTIFICATIONS, FULL_SCREEN_INTENT;

    fun launchActivity(context: Context, permissionViews: ArrayList<PermissionView>) {
        when (getCurrentPermissionStatus(context)) {
            PermissionStatus.NOT_VALID -> {
                context.startActivity(this.buildIntent(context, permissionViews))
            }
            PermissionStatus.WARNING -> {
                this.getIgnoreSharedPrefsKey()?.let {
                    if (DriveKitSharedPreferencesUtils.getBoolean(it, false)) {
                        launchNextPermission(context, permissionViews)
                    } else {
                        context.startActivity(this.buildIntent(context, permissionViews))
                    }
                } ?: run {
                    launchNextPermission(context, permissionViews)
                }
            }
            PermissionStatus.VALID -> {
                launchNextPermission(context, permissionViews)
            }
        }
    }

    fun ignore() {
        getIgnoreSharedPrefsKey()?.let { sharedPrefsKey ->
            DriveKitSharedPreferencesUtils.setBoolean(sharedPrefsKey, true)
        }
    }

    private fun getIgnoreSharedPrefsKey() = when (this) {
        NOTIFICATIONS -> "dk_ignore_permission_notifications_key"
        FULL_SCREEN_INTENT -> "dk_ignore_full_screen_intent_notifications_key"
        ACTIVITY,
        LOCATION,
        BACKGROUND_TASK,
        NEARBY_DEVICES -> null
    }

    private fun launchNextPermission(context: Context, permissionViews: ArrayList<PermissionView>) {
        permissionViews.remove(this)
        if (permissionViews.isEmpty()) {
            PermissionsUtilsUI.permissionViewListener?.onFinish()
        } else {
            permissionViews.first().launchActivity(context, permissionViews)
        }
    }

    private fun getCurrentPermissionStatus(context: Context): PermissionStatus {
        return when (this) {
            NOTIFICATIONS -> DiagnosisHelper.getNotificationStatus(context)
            LOCATION -> DiagnosisHelper.getLocationStatus(context)
            ACTIVITY -> DiagnosisHelper.getActivityStatus(context)
            BACKGROUND_TASK -> DiagnosisHelper.getBatteryOptimizationsStatus(context)
            NEARBY_DEVICES -> DiagnosisHelper.getNearbyDevicesStatus(context)
            FULL_SCREEN_INTENT -> DiagnosisHelper.getFullScreenIntentStatus(context)
        }
    }

    @SuppressLint("NewApi")
    private fun buildIntent(context: Context, permissionViews: ArrayList<PermissionView>): Intent {
        val selectedClass = when (this) {
            NOTIFICATIONS -> NotificationsPermissionActivity::class.java
            LOCATION -> LocationPermissionActivity::class.java
            ACTIVITY -> ActivityRecognitionPermissionActivity::class.java
            BACKGROUND_TASK -> BackgroundTaskPermissionActivity::class.java
            NEARBY_DEVICES -> NearbyDevicesPermissionActivity::class.java
            FULL_SCREEN_INTENT -> FullScreenIntentPermissionActivity::class.java
        }
        val intent = Intent(context, selectedClass)
        intent.putExtra(PERMISSION_VIEWS_LIST_EXTRA, permissionViews)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        return intent
    }
}
