package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import com.drivequant.drivekit.vehicle.ui.R

enum class VehicleType {
    CAR,
    MOTORBIKE,
    TRUCK;

    fun getTitle(context: Context): String? {
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
            MOTORBIKE -> listOf()
            TRUCK -> listOf()
        }
    }

    fun getBrands(): List<String> {
        return listOf()
    }
}