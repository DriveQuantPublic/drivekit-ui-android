package com.drivequant.drivekit.permissionsutils.diagnosis

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.util.Log
import androidx.annotation.RequiresApi
import com.drivequant.drivekit.common.ui.utils.DKReachability
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionType
import com.drivequant.drivekit.permissionsutils.diagnosis.model.SensorType


/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

object DiagnosisHelper {

    const val REQUEST_PERMISSIONS = 1
    const val REQUEST_PERMISSIONS_OPEN_SETTINGS = 3
    const val REQUEST_BATTERY_OPTIMIZATION = 4

    fun hasCoarseLocationPermission(context: Context): Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    fun hasFineLocationPermission(context: Context): Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.Q)
    fun hasBackgroundLocationApproved(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context.applicationContext,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun getLocationStatus(context: Context): PermissionStatus {
        if (!hasFineLocationPermission(context)) {
            return PermissionStatus.NOT_VALID
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!hasBackgroundLocationApproved(context)) {
                return PermissionStatus.NOT_VALID
            }
        }
        return PermissionStatus.VALID
    }

    fun getActivityStatus(context: Context): PermissionStatus {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                PermissionStatus.VALID
            } else {
                PermissionStatus.NOT_VALID
            }
        } else {
            PermissionStatus.VALID
        }
    }

    fun getAutoResetStatus(context: Context): PermissionStatus {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (context.packageManager.isAutoRevokeWhitelisted) {
                PermissionStatus.VALID
            } else {
                PermissionStatus.NOT_VALID
            }
        } else {
            PermissionStatus.VALID
        }
    }

    fun getNearbyDevicesStatus(context: Context): PermissionStatus =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                PermissionStatus.VALID
            } else {
                PermissionStatus.NOT_VALID
            }
        } else {
            PermissionStatus.VALID
        }

    fun getBatteryOptimizationsStatus(context: Context): PermissionStatus {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.applicationContext?.let {
                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                val packageName = context.packageName
                return if (pm.isIgnoringBatteryOptimizations(packageName)) PermissionStatus.VALID else PermissionStatus.NOT_VALID
            }
            return PermissionStatus.NOT_VALID
        } else {
            return PermissionStatus.VALID
        }
    }

    fun requestBatteryOptimization(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent()
                val packageName = activity.packageName
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                activity.startActivityForResult(intent, REQUEST_BATTERY_OPTIMIZATION)
            } catch (e: ActivityNotFoundException) {
                // Catch crashes on some Samsung devices
            }
        }
    }

    fun getNotificationStatus(context: Context) =
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            PermissionStatus.VALID
        } else {
            PermissionStatus.NOT_VALID
        }

    fun getPermissionStatus(context: Context, permissionType: PermissionType) =
        when (permissionType) {
            PermissionType.LOCATION -> getLocationStatus(context)
            PermissionType.ACTIVITY -> getActivityStatus(context)
            PermissionType.NOTIFICATION -> getNotificationStatus(context)
            PermissionType.AUTO_RESET -> getAutoResetStatus(context)
            PermissionType.NEARBY -> getNearbyDevicesStatus(context)
        }

    fun buildSettingsIntent(context: Context): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        return intent
    }

    fun isSensorActivated(context: Context, sensorType: SensorType) = when (sensorType) {
        SensorType.BLUETOOTH -> {
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter?.isEnabled ?: false
        }
        SensorType.GPS -> {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val isGPSEnabled =
                locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
            isGPSEnabled && isLocationSensorHighAccuracy(context, isGPSEnabled)
        }
    }

    fun isLocationSensorHighAccuracy(context: Context, isGPSEnabled: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            return isGPSEnabled && locationManager?.isLocationEnabled ?: run { false }
        } else {
            try {
                val locationAccuracyLevel = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE
                )
                return isGPSEnabled && locationAccuracyLevel == 3
            } catch (e: Settings.SettingNotFoundException) {
                Log.e("DiagnosticHelper", "Could not retrieve location accuracy level")
            }
        }
        return false
    }

    fun isNetworkReachable(context: Context) = DKReachability().isConnectedToNetwork(context)
}