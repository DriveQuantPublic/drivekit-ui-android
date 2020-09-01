package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import com.drivequant.drivekit.common.ui.adapter.FilterItem

interface VehicleUIEntryPoint {
    fun startVehicleListActivity(context: Context)
    fun startVehicleDetailActivity(context: Context, vehicleId: String) : Boolean
    fun getVehicleInfoById(context: Context, vehicleId: String, listener : GetVehicleInfoByVehicleIdListener)
    fun getVehiclesFilterItems(vehiclesFilterItems: List<FilterItem>)
}

interface GetVehiclesFilterItems {
    fun onFilterItemsReceived(vehiclesFilterItems: List<FilterItem>)
}

interface GetVehicleInfoByVehicleIdListener {
    fun onVehicleInfoRetrieved(vehicleName: String, liteConfig: Boolean?)
}