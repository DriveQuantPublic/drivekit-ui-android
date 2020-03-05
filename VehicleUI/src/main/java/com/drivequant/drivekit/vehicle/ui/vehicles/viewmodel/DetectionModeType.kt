package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import com.drivequant.drivekit.vehicle.ui.R

// TODO put configured & unconfigured desriptions ?
enum class DetectionModeType(
    val title: Int
) {
    DISABLED(R.string.dk_detection_mode_disabled_title),
    GPS(R.string.dk_detection_mode_gps_title),
    BEACON(R.string.dk_detection_mode_beacon_title),
    BLUETOOTH(R.string.dk_detection_mode_bluetooth_title);
}