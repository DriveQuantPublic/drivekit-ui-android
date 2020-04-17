package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Vehicle

interface VehicleActionItem {
    fun getTitle(context: Context): String
    fun isDisplayable(vehicle: Vehicle): Boolean
    fun onItemClicked(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle)
}