package com.drivequant.drivekit.common.ui.navigation

import android.content.Context

interface VehicleUIEntryPoint {
    fun startVehicleListActivity(context: Context)
    fun startVehicleDetailActivity(context: Context, vehicleId: String)
}