package com.drivequant.drivekit.common.ui.navigation

import android.content.Context
import android.support.v4.app.Fragment
import java.io.Serializable

interface VehicleUIEntryPoint {
    fun startVehicleListActivity(context: Context)
    fun createVehicleListFragment(): Fragment

    fun startVehicleDetailActivity(context: Context)
    fun createVehicleDetailFragment(): Fragment

    fun createVehiclePickerActivity(context: Context)
    fun createVehiclePickerFragment(description: String, vehiclePickerStep: Int, items: Serializable): Fragment
}