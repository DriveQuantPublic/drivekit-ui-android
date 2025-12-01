package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.adapter.FilterItem

interface VehicleUIEntryPoint {
    fun startVehicleListActivity(context: Context)
    fun startVehicleDetailActivity(context: Context, vehicleId: String): Boolean
    fun startFindMyVehicleActivity(context: Context)
    fun getVehicleInfoById(context: Context, vehicleId: String, listener : GetVehicleInfoByVehicleIdListener)
    fun getVehiclesFilterItems(context: Context): List<FilterItem>
    fun createVehicleListFragment(): Fragment
}

interface GetVehicleInfoByVehicleIdListener {
    fun onVehicleInfoRetrieved(vehicleName: String, liteConfig: Boolean?)
}