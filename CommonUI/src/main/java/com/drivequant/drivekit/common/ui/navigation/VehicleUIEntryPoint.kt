package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import android.support.v4.app.Fragment

interface VehicleUIEntryPoint {
    fun startVehicleListActivity(context: Context)
    fun createVehicleListFragment(): Fragment

    fun createVehiclePickerActivity(context: Context)

    fun createVehicleDetailFragment(vehicleId: String): Fragment
    fun createVehicleDetailActivity(context: Context, vehicleId: String)

    fun createBluetoothActivity(context: Context, vehicleId: String)
}