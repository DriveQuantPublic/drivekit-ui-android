package com.drivequant.drivekit.vehicle.ui.listener

interface OnCameraPictureTakenCallback {
    fun pictureTaken(filePath: String)
    fun onFilePathReady(filePath: String)
}