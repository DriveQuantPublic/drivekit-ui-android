package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils

internal class OdometerDetailViewModel(val vehicleId: String) : ViewModel() {

    fun shouldShowDisplayReadingButton() = DriveKitVehicle.odometerHistoriesQuery().whereEqualTo("vehicleId", vehicleId).countQuery()
        .execute() > 0

    fun getVehicleDisplayName(context: Context) = DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()?.let {
        VehicleUtils().buildFormattedName(context, it)
    }

    fun getVehicleDrawable(context:Context) = VehicleUtils().getVehicleDrawable(context, vehicleId)

    @Suppress("UNCHECKED_CAST")
    internal class OdometerDetailViewModelFactory(private val vehicleId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OdometerDetailViewModel(vehicleId) as T
        }
    }
}
