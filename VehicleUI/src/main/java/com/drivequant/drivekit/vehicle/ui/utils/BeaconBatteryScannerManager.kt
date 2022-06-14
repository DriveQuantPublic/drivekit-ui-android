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

    private var beaconBatteryReaderScanner: BeaconInfoReaderScanner? = null

    fun startBatteryReaderScanner() {
        beaconBatteryReaderScanner = BeaconInfoReaderScanner()
        beaconBatteryReaderScanner?.registerListener(this, context)
    }

    fun stopBatteryReaderScanner() {
        beaconBatteryReaderScanner?.unregisterListener(context)
        beaconBatteryReaderScanner = null
    }

    override fun onBeaconInfoRetrieved(
        batteryLevel: Int,
        estimatedDistance: Double, rssi: Int, txPower: Int
    ) {
        listener.onBeaconInfoRetrieved(DKBeaconInfo(batteryLevel, estimatedDistance, rssi, txPower))
    }

    override fun getBeacon(): BeaconData = beaconData
}

interface DKBeaconInfoListener {
    fun onBeaconInfoRetrieved(dkBeaconInfo: DKBeaconInfo)
}

class DKBeaconInfo(
    val batteryLevel: Int,
    val estimatedDistance: Double,
    val rssi: Int,
    val txPower: Int
)