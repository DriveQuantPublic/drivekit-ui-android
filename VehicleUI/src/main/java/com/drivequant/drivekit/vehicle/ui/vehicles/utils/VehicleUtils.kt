package com.drivequant.drivekit.vehicle.ui.vehicles.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import android.text.TextUtils
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKitSharedPreferencesUtils
import com.drivequant.drivekit.databaseutils.Query
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.extension.getDefaultImage
import java.util.*


open class VehicleUtils {

    fun fetchVehiclesOrderedByDisplayName(context: Context) : List<Vehicle> {
        val sortedVehicles = DriveKitVehicle.vehiclesQuery().noFilter()
            .orderBy("brand", Query.Direction.ASCENDING)
            .query()
            .execute()
            .sortedWith(compareBy(Vehicle::brand, Vehicle::model, Vehicle::version))
            .toMutableList()

        sortedVehicles.sortWith(Comparator { vehicle1: Vehicle, vehicle2: Vehicle ->
            val vehicle1DisplayName = buildFormattedNameByPosition(context, vehicle1, sortedVehicles)
            val vehicle2DisplayName = buildFormattedNameByPosition(context, vehicle2, sortedVehicles)
            vehicle1DisplayName.compareTo(vehicle2DisplayName, ignoreCase = true)
        })
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
            vehicle.name?.let { it }?:run { " " }
        } else {
            val vehicleNumber: Int = pos + 1
            val myVehicleString = DKResource.convertToString(context, "dk_vehicle_my_vehicle")
            "$myVehicleString - $vehicleNumber"
        }
    }

    fun getVehicleDrawable(context: Context, vehicleId: String?): Drawable? {
        val vehicle =  vehicleId?.let {
            DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne()
                .executeOne()
        }?: kotlin.run {
            null
        }

        val defaultVehicleDrawable = vehicle?.let {
            ResourcesCompat.getDrawable(
                context.resources,
                it.getDefaultImage(),
                null
            )
        }?:run {
            ContextCompat.getDrawable(context, R.drawable.dk_my_trips)
        }

       val cameraFilePath = DriveKitSharedPreferencesUtils.getString(String.format("drivekit-vehicle-picture_%s", vehicleId))
        return if (!TextUtils.isEmpty(cameraFilePath)) {
            val uri = Uri.parse(cameraFilePath)
            val stream = context.contentResolver.openInputStream(uri)

            val b = BitmapFactory.decodeStream(stream)
            b.density = Bitmap.DENSITY_NONE
            BitmapDrawable(b)
        } else {
            defaultVehicleDrawable
        }
    }
}