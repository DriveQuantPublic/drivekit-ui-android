package com.drivequant.drivekit.vehicle.ui.beacon.viewmodel

interface ScanState {
    fun onStateUpdated(step: BeaconStep)
    fun onScanFinished()
    fun displayLoader()
    fun hideLoader()
}