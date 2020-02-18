package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.commons.MediaUtils
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem

enum class VehicleType {
    CAR,
    MOTORBIKE,
    TRUCK;

    fun getTitle(context: Context): String {
        return when (this){
            CAR -> context.getString(R.string.dk_vehicle_type_car_title)
            MOTORBIKE -> context.getString(R.string.dk_vehicle_type_motorbike_title)
            TRUCK -> context.getString(R.string.dk_vehicle_type_truck_title)
        }
    }

    fun getImageResource(): Int? {
        return null
    }

    fun getCategories(): List<CarCategory> {
        return when (this){
            CAR -> { listOf(CarCategory.MICRO,
                        CarCategory.COMPACT,
                        CarCategory.SEDAN,
                        CarCategory.SUV,
                        CarCategory.MINIVAN,
                        CarCategory.LUXURY,
                        CarCategory.SPORT)
            }
            MOTORBIKE -> listOf(CarCategory.SPORT)
            TRUCK -> listOf(CarCategory.MINIVAN)
        }
    }

    fun getBrands(context: Context, withIcons: Boolean = false): List<VehicleBrandItem> {
        val lines = MediaUtils.readCSVFile(context, R.raw.vehicle_brands, delimiter = ";")
        val brands = mutableListOf<VehicleBrandItem>()
        for (i in lines!!.indices) {
            val vehicleBrandItem = buildFromCSV(context, lines[i])
            if (!withIcons || (withIcons && vehicleBrandItem.icon != null)){
                brands.add(vehicleBrandItem)
            }
        }
        return when (this){
            CAR -> brands.filter { it.isCar}
            MOTORBIKE -> brands.filter { it.isMotorbike}
            TRUCK -> brands.filter { it.isTruck}
        }
    }


}