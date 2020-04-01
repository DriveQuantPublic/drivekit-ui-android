package com.drivequant.drivekit.vehicle.ui.vehicles.utils

import android.content.Context
import android.text.TextUtils
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import java.util.*

open class VehicleUtils {

    fun fetchVehiclesOrderedByDisplayName(
        context: Context,
        synchronizationType: SynchronizationType,
        listener : VehiclesFetchListener)
    {
        DriveKitVehicle.getVehiclesOrderByNameAsc(object : VehicleListQueryListener{
            override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                val sortedVehicles = vehicles.toMutableList()
                sortedVehicles.sortWith(Comparator { vehicle1: Vehicle, vehicle2: Vehicle ->
                    val vehicle1Pos: Int = getVehiclePositionInList(vehicle1, vehicles)
                    val vehicle2Pos: Int = getVehiclePositionInList(vehicle2, vehicles)
                    val vehicle1DisplayName = buildFormattedNameByPosition(context, vehicle1, vehicle1Pos)
                    val vehicle2DisplayName = buildFormattedNameByPosition(context, vehicle2, vehicle2Pos)
                    vehicle1DisplayName.compareTo(vehicle2DisplayName, ignoreCase = true)
                })
                listener.onVehiclesLoaded(status, sortedVehicles)
            }
        }, synchronizationType)
    }

    fun getVehiclePositionInList(vehicle: Vehicle, allVehicles: List<Vehicle>): Int {
        for (i in allVehicles.indices) {
            if (allVehicles[i].vehicleId == vehicle.vehicleId) {
                return i
            }
        }
        return -1
    }

    private fun buildFormattedNameByPosition(context: Context, vehicle: Vehicle, pos: Int): String {
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
            it.equals(getDefaultName(vehicle), true) ||
                    it.equals("${vehicle.model} ${vehicle.version}", true)
        }?:run {
            true
        }
    }

    private fun getDefaultName(vehicle: Vehicle) : String {
        return "${vehicle.brand} ${vehicle.model} ${vehicle.version}"
    }
}

interface VehiclesFetchListener {
    fun onVehiclesLoaded(syncStatus: VehicleSyncStatus, vehicles: List<Vehicle>)
}