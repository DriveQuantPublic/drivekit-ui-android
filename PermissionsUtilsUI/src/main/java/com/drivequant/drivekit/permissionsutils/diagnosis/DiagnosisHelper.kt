package com.drivequant.drivekit.permissionsutils.diagnosis

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKUtils


/**
 * Created by Mohamed on 2020-04-02.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

object DiagnosisHelper {

    const val REQUEST_PERMISSIONS = 1
    const val REQUEST_PERMISSIONS_OPEN_SETTINGS = 3
    const val REQUEST_BATTERY_OPTIMIZATION = 4

    const val PERMISSION_BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    const val PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    const val PERMISSION_ACTIVITY_RECOGNITION = Manifest.permission.ACTIVITY_RECOGNITION

    fun hasFineLocationPermission(activity: Activity): Boolean = ActivityCompat.checkSelfPermission(activity, PERMISSION_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun hasBackgroundLocationApproved(activity: Activity) : Boolean = ActivityCompat.checkSelfPermission(activity, PERMISSION_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun isLocationAuthorize(activity: Activity): Boolean {
        if (!hasFineLocationPermission(activity)) {
            return false
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!hasBackgroundLocationApproved(activity)) {
                return false
            }
        }
        return true
    }

    fun isActivityRecognitionAuthorize(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isIgnoringBatteryOptimizations(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                activity.applicationContext?.let {
                    val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
                    val packageName = activity.packageName
                    return pm.isIgnoringBatteryOptimizations(packageName)
                }
            } catch (e: ActivityNotFoundException) {
                // Catch crashes on some Samsung devices
            }
            return true
        } else {
            return true
        }
    }

    fun isNotificationAuthorize(activity: Activity): Boolean = NotificationManagerCompat.from(activity).areNotificationsEnabled()

    fun getPermissionStatus(activity: Activity, permissionType: PermissionType): PermissionStatus {
        return when (permissionType) {
            PermissionType.LOCATION -> if (isLocationAuthorize(activity)) PermissionStatus.VALID else PermissionStatus.NOT_VALID

            PermissionType.ACTIVITY -> if (isActivityRecognitionAuthorize(activity)) PermissionStatus.VALID else PermissionStatus.NOT_VALID

            PermissionType.NOTIFICATION -> if (isNotificationAuthorize(activity)) PermissionStatus.VALID else PermissionStatus.NOT_VALID

            PermissionType.EXTERNAL_STORAGE -> PermissionStatus.NOT_VALID //TODO
        }
    }

    @SuppressLint("MissingPermission")
    fun isSensorActivated(context: Context, sensorType: SensorType): Boolean {
         return when(sensorType){
            SensorType.BLUETOOTH -> {
                val bluetoothAdapter:BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
                bluetoothAdapter?.isEnabled ?: false
            }
            SensorType.GPS -> {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
            }
        }
    }

    fun isLocationSensorHighAccuracy(context: Context, isGPSEnabled: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                return isGPSEnabled && locationManager?.isLocationEnabled ?: run { false }
            } else {
                try {
                    val locationAccuracyLevel = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
                    return isGPSEnabled && locationAccuracyLevel == 3
                } catch (e: Settings.SettingNotFoundException) {
                    Log.e("DiagnosticHelper", "Could not retrieve location accuracy level")
                }
            }
        }
        return false
    }

    fun isNetworkReachable(context: Context): Boolean = DKUtils.isInternetAvailable(context)
}