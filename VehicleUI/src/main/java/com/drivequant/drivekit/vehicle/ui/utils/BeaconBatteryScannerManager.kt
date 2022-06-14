package com.drivequant.drivekit.vehicle.ui.utils

import android.content.Context
import com.drivequant.beaconutils.BeaconData
import com.drivequant.beaconutils.BeaconInfoReaderListener
import com.drivequant.beaconutils.BeaconInfoReaderScanner

internal class BeaconInfoScannerManager(
    private val context: Context,
    private val beaconData: BeaconData,
    private val listener: DKBeaconInfoListener
) : BeaconInfoReaderListener {

    private var beaconBatteryReaderScanner: BeaconInfoReaderScanner =
        BeaconInfoReaderScanner()

    fun startBatteryReaderScanner() {
        beaconBatteryReaderScanner.registerListener(this, context)
    }

    fun stopBatteryReaderScanner() {
        beaconBatteryReaderScanner.unregisterListener(context)
    }

    override fun onBeaconInfoRetrieved(batteryLevel: Int, estimatedDistance: Double, rssi: Int) {
        listener.onBeaconInfoRetrieved(batteryLevel, estimatedDistance, rssi)
    }

    override fun getBeacon(): BeaconData = beaconData
}

interface DKBeaconInfoListener {
    fun onBeaconInfoRetrieved(batteryLevel: Int, estimatedDistance: Double, rssi: Int)
}
