package com.drivequant.drivekit.permissionsutils.diagnosisnotification

import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.deviceconfiguration.DKDeviceConfigurationEvent
import com.drivequant.drivekit.core.module.BluetoothUsage
import com.drivequant.drivekit.core.utils.ConnectivityType
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.core.utils.PermissionStatus
import com.drivequant.drivekit.core.utils.PermissionType
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosisnotification.model.DKDiagnosisNotificationInfo

internal object DKDeviceConfigurationEventNotificationManager {

    fun getNotificationInfo(): DKDiagnosisNotificationInfo? = getInvalidNotifiableEvents().toDKDiagnosisNotificationInfo()

    private fun getInvalidNotifiableEvents(): List<DKDeviceConfigurationEvent> {
        val context = DriveKit.applicationContext
        val events = mutableListOf<DKDeviceConfigurationEvent>()
        val bluetoothItemRequired = DriveKit.modules.tripAnalysis?.getBluetoothUsage() == BluetoothUsage.REQUIRED

        // Location sensor
        if (!DiagnosisHelper.isActivated(context, ConnectivityType.GPS)) {
            events.add(DKDeviceConfigurationEvent.LocationSensor(false))
        }

        // Bluetooth sensor
        if (!DiagnosisHelper.isActivated(context, ConnectivityType.BLUETOOTH) && bluetoothItemRequired) {
            events.add(DKDeviceConfigurationEvent.BluetoothSensor(false))
        }

        // Location permission
        if (DiagnosisHelper.getPermissionStatus(context, PermissionType.LOCATION) != PermissionStatus.VALID) {
            events.add(DKDeviceConfigurationEvent.LocationPermission(false))
        }

        // Nearby devices permission
        if (DiagnosisHelper.getPermissionStatus(context, PermissionType.NEARBY) != PermissionStatus.VALID && bluetoothItemRequired) {
            events.add(DKDeviceConfigurationEvent.NearbyDevicesPermission(false))
        }

        // Physical Activity permission
        if (DiagnosisHelper.getPermissionStatus(context, PermissionType.ACTIVITY) != PermissionStatus.VALID) {
            events.add(DKDeviceConfigurationEvent.ActivityPermission(false))
        }

        // Battery optimization permission
        if (DiagnosisHelper.getBatteryOptimizationsStatus(context) != PermissionStatus.VALID) {
            events.add(DKDeviceConfigurationEvent.AppBatteryOptimisation(false))
        }

        // Notification permission: do not check
        // Auto-reset permission: do not check

        return events
    }

    private fun List<DKDeviceConfigurationEvent>.toDKDiagnosisNotificationInfo(): DKDiagnosisNotificationInfo? {
        val eventsCount = this.size
        val messageId = when {
            eventsCount > 1 -> R.string.dk_perm_utils_notification_multiple_issues
            eventsCount == 1 -> {
                when (val event = this.first()) {
                    is DKDeviceConfigurationEvent.LocationSensor -> R.string.dk_perm_utils_notification_location_sensor
                    is DKDeviceConfigurationEvent.BluetoothSensor -> R.string.dk_perm_utils_notification_bluetooth_sensor
                    is DKDeviceConfigurationEvent.LocationPermission -> R.string.dk_perm_utils_notification_location_permission
                    is DKDeviceConfigurationEvent.ActivityPermission -> R.string.dk_perm_utils_notification_activity_recognition_permission
                    is DKDeviceConfigurationEvent.AppBatteryOptimisation -> R.string.dk_perm_utils_notification_app_battery_optimization
                    is DKDeviceConfigurationEvent.NearbyDevicesPermission -> R.string.dk_perm_utils_notification_nearby_devices_permission
                    is DKDeviceConfigurationEvent.AutoResetPermission,
                    is DKDeviceConfigurationEvent.NotificationPermission -> throw Exception("The event $event is not managed")
                }
            }
            else -> return null
        }
        return DKDiagnosisNotificationInfo(messageId)
    }
}