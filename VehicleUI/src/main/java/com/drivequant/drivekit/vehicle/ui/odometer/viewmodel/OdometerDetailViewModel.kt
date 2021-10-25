package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.VehicleOdometer
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import java.util.*

class OdometerDetailViewModel(val vehicleId: String) : ViewModel() {

    var vehicleOdometer: VehicleOdometer? = null

    init {
        vehicleOdometer =
            DriveKitVehicle.odometerQuery().whereEqualTo("vehicleId", vehicleId).queryOne()
                .executeOne()
    }

    fun getVehicleDisplayName(context: Context) = DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()?.let {
        VehicleUtils().buildFormattedName(context, it)
    }

    fun getVehicleDrawable(context:Context) = VehicleUtils().getVehicleDrawable(context, vehicleId)

    @Suppress("UNCHECKED_CAST")
    class OdometerDetailViewModelFactory(private val vehicleId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return OdometerDetailViewModel(vehicleId) as T
        }
    }
}