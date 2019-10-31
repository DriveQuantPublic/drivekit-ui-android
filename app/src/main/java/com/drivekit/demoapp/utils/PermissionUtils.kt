package com.drivekit.demoapp.utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.content.ContextCompat.startActivity

class PermissionUtils {

    fun isLocationAuthorize(activity: Activity): Boolean{
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFineLocationPermission) {
            return false
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val hasBackgroundLocationPermission = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
            if (!hasBackgroundLocationPermission) {
                return false
            }
        }
        return true
    }

    fun checkLocationPermission(activity: Activity, request: Int): Boolean{
        val permissionFineLocationApproved = ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (permissionFineLocationApproved) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
                if (backgroundLocationPermissionApproved) {
                    true
                } else {
                    ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        request)
                    false
                }
            } else {
                true
            }
        } else {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    request)
                false
            } else {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    request)
                false
            }
        }
    }

    fun isActivityRecognitionAuthorize(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun checkActivityRecognitionPermission(activity: Activity, request: Int): Boolean {
        return if (!isActivityRecognitionAuthorize(activity)
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACTIVITY_RECOGNITION)) {
                false
            } else {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    request)
                false
            }
        } else{
            true
        }
    }

    fun isLoggingAuthorize(activity: Activity): Boolean{
        return ContextCompat.checkSelfPermission(activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun checkLoggingPermission(activity: Activity, request: Int): Boolean {
        return if (!isLoggingAuthorize(activity)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                false
            } else {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    request)
                false
            }
        } else{
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
            } catch (e: ActivityNotFoundException){
                // Catch crashes on some Samsung devices
            }
            return true
        } else {
            return true
        }
    }

    fun checkBatteryOptimization(activity: Activity): Boolean{
        return if (!isIgnoringBatteryOptimizations(activity)){
            val intent = Intent()
            val packageName = activity.packageName
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            activity.startActivity(intent)
            false
        } else {
            true
        }
    }
}