package com.drivequant.drivekit.vehicle.ui.vehicles.utils

import android.content.Context
import android.text.TextUtils
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import java.util.*

open class VehicleUtils {

    fun fetchVehiclesOrderedByDisplayName(context: Context) : List<Vehicle> {
        val sortedVehicles = DriveKitVehicle.getVehiclesInDatabase().toMutableList()
        sortedVehicles.sortWith(Comparator { vehicle1: Vehicle, vehicle2: Vehicle ->
            val vehicle1DisplayName = buildFormattedNameByPosition(context, vehicle1, sortedVehicles)
            val vehicle2DisplayName = buildFormattedNameByPosition(context, vehicle2, sortedVehicles)
            vehicle1DisplayName.compareTo(vehicle2DisplayName, ignoreCase = true)
        })
        return sortedVehicles
    }

    private fun buildFormattedNameByPosition(context: Context, vehicle: Vehicle, vehicles: List<Vehicle>): String {
        val pos = vehicles.indexOf(vehicle)
        return if (!TextUtils.isEmpty(vehicle.name) && !isNameEqualsDefaultName(vehicle)) {
            vehicle.name?.let { it }?:run { " " }
        } else {
            val vehicleNumber: Int = pos + 1
            val myVehicleString = DKResource.convertToString(context, "dk_vehicle_my_vehicle")
            "$myVehicleString - $vehicleNumber"
        }
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
}