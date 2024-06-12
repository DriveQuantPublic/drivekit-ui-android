package com.drivequant.drivekit.vehicle.ui.vehicles.utils

import android.content.Context
import android.text.TextUtils
import androidx.annotation.DrawableRes
import com.drivequant.drivekit.databaseutils.Query
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.extension.getImageByTypeIndex


object VehicleUtils {

    fun getVehicleById(vehicleId: String) =
        DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()

    fun fetchVehiclesOrderedByDisplayName(context: Context) : List<Vehicle> {
        val sortedVehicles = DriveKitVehicle.vehiclesQuery().noFilter()
            .orderBy("brand", Query.Direction.ASCENDING)
            .query()
            .execute()
            .sortedWith(compareBy(Vehicle::brand, Vehicle::model, Vehicle::version))
            .toMutableList()

        sortedVehicles.sortWith { vehicle1: Vehicle, vehicle2: Vehicle ->
            val vehicle1DisplayName = buildFormattedNameByPosition(
                context,
                vehicle1,
                sortedVehicles
            )
            val vehicle2DisplayName = buildFormattedNameByPosition(
                context,
                vehicle2,
                sortedVehicles
            )
            vehicle1DisplayName.compareTo(vehicle2DisplayName, ignoreCase = true)
        }
        return sortedVehicles
    }

    fun buildFormattedName(context: Context, vehicle: Vehicle) : String {
        return vehicle.buildFormattedName(context)
    }

    fun isNameEqualsDefaultName(vehicle: Vehicle): Boolean {
        return vehicle.name?.let {
            val defaultName = "${vehicle.brand} ${vehicle.model} ${vehicle.version}"
            it.equals(defaultName, true) ||
                    it.equals("${vehicle.model} ${vehicle.version}", true)
        }?:run {
            true
        }
    }

    private fun buildFormattedNameByPosition(context: Context, vehicle: Vehicle, vehicles: List<Vehicle>): String {
        val pos = vehicles.indexOf(vehicle)
        return if (!TextUtils.isEmpty(vehicle.name) && !isNameEqualsDefaultName(vehicle)) {
            vehicle.name ?: " "
        } else {
            val vehicleNumber: Int = pos + 1
            val myVehicleString = context.getString(R.string.dk_vehicle_my_vehicle)
            "$myVehicleString - $vehicleNumber"
        }
    }

    @DrawableRes
    fun getFilterVehicleDrawable(vehicleId: String) =
        getFilterVehicleDrawable(getVehicleById(vehicleId))

    @DrawableRes
    fun getFilterVehicleDrawable(vehicle: Vehicle?) =
        vehicle?.getImageByTypeIndex() ?: com.drivequant.drivekit.common.ui.R.drawable.dk_my_trips
}
