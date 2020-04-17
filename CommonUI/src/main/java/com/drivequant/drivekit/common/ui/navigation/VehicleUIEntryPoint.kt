package com.drivequant.drivekit.common.ui.navigation

import android.content.Context

interface VehicleUIEntryPoint {
    fun startVehicleListActivity(context: Context)
    fun startVehicleDetailActivity(context: Context, vehicleId: String) : Boolean
    fun getVehicleInfoById(context: Context, vehicleId: String, listener : GetVehicleInfoByVehicleIdListener)
}

interface GetVehicleInfoByVehicleIdListener {
    fun onVehicleInfoRetrieved(vehicleName: String, liteConfig: Boolean?)
}