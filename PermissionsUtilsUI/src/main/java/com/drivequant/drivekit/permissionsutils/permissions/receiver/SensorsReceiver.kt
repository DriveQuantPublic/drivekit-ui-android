package com.drivequant.drivekit.permissionsutils.permissions.receiver

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import com.drivequant.drivekit.core.utils.ConnectivityType
import com.drivequant.drivekit.core.utils.DiagnosisHelper

/**
 * Created by Mohamed on 2020-04-16.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

abstract class SensorsReceiver : BroadcastReceiver() {

    companion object {
        const val LOCATION_INTENT_ACTION = LocationManager.PROVIDERS_CHANGED_ACTION
        const val BLUETOOTH_INTENT_ACTION = BluetoothAdapter.ACTION_STATE_CHANGED
        const val CONNECTIVITY_INTENT_ACTION = ConnectivityManager.CONNECTIVITY_ACTION
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            init(it)
        }

        intent?.let {
            context?.let { context ->
                when (it.action) {
                    LOCATION_INTENT_ACTION  -> getGpsState(DiagnosisHelper.isActivated(context, ConnectivityType.GPS))
                    CONNECTIVITY_INTENT_ACTION -> getConnectivityState(DiagnosisHelper.isNetworkReachable(context))
                    BLUETOOTH_INTENT_ACTION -> getBluetoothState(DiagnosisHelper.isActivated(context, ConnectivityType.BLUETOOTH))
                }
            }
        }
        onSensorBroadcastReceived()
    }

    abstract fun getGpsState(gpsState:Boolean)

    abstract fun getBluetoothState(bluetoothState: Boolean)

    abstract fun getConnectivityState(connectivityState: Boolean)

    abstract fun onSensorBroadcastReceived()

    private fun init(context: Context) {
        getGpsState(DiagnosisHelper.isActivated(context, ConnectivityType.GPS))
        getConnectivityState(DiagnosisHelper.isNetworkReachable(context))
        getBluetoothState(DiagnosisHelper.isActivated(context, ConnectivityType.BLUETOOTH))
    }
}
