package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import android.support.v4.app.Fragment

interface VehicleUIEntryPoint {
    fun startVehicleListActivity(context: Context)
    fun createVehicleListFragment(): Fragment

    fun createVehiclePickerActivity(context: Context)
    fun createVehicleDetailActivity(context: Context, vehicleId: String)
}