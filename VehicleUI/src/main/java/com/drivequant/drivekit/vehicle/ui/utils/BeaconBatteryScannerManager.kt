package com.drivequant.drivekit.vehicle.ui.utils

import android.content.Context
import com.drivequant.beaconutils.BeaconBatteryReaderListener
import com.drivequant.beaconutils.BeaconBatteryReaderScanner
import com.drivequant.beaconutils.BeaconData

internal class BeaconBatteryScannerManager(
    private val context: Context,
    private val beaconData: BeaconData,
    private val listener: BatteryLevelReadListener
) : BeaconBatteryReaderListener {

    private var beaconBatteryReaderScanner: BeaconBatteryReaderScanner =
        BeaconBatteryReaderScanner()

    fun startBatteryReaderScanner() {
        beaconBatteryReaderScanner.registerListener(this, context)
    }

    fun stopBatteryReaderScanner() {
        beaconBatteryReaderScanner.unregisterListener(context)
    }

    override fun onBatteryLevelRead(batteryLevel: Int) {
        listener.onBatteryLevelRead(batteryLevel)
    }

    override fun getBeacon(): BeaconData = beaconData
}

interface BatteryLevelReadListener {
    fun onBatteryLevelRead(batteryLevel: Int)
}
