package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import com.drivequant.drivekit.vehicle.ui.R

enum class CarCategory {
    MICRO,
    COMPACT,
    SEDAN,
    SUV,
    MINIVAN,
    COMMERCIAL,
    LUXURY,
    SPORT;

    fun getTitle(context: Context): String {
        return when (this){
            MICRO -> context.getString(R.string.dk_vehicle_category_car_micro_title)
            COMPACT -> context.getString(R.string.dk_vehicle_category_car_compact_title)
            SEDAN -> context.getString(R.string.dk_vehicle_category_car_sedan_title)
            SUV -> context.getString(R.string.dk_vehicle_category_car_suv_title)
            MINIVAN -> context.getString(R.string.dk_vehicle_category_car_minivan_title)
            COMMERCIAL -> context.getString(R.string.dk_vehicle_category_car_commercial_title)
            LUXURY -> context.getString(R.string.dk_vehicle_category_car_luxury_title)
            SPORT -> context.getString(R.string.dk_vehicle_category_car_sport_title)
        }
    }

    fun getImageResource() : Int {
        return when (this){
            MICRO -> R.string.dk_vehicle_category_car_micro_title
            COMPACT -> R.string.dk_vehicle_category_car_compact_title
            SEDAN -> R.string.dk_vehicle_category_car_sedan_title
            SUV -> R.string.dk_vehicle_category_car_suv_title
            MINIVAN -> R.string.dk_vehicle_category_car_minivan_title
            COMMERCIAL -> R.string.dk_vehicle_category_car_commercial_title
            LUXURY -> R.string.dk_vehicle_category_car_luxury_title
            SPORT -> R.string.dk_vehicle_category_car_sport_title
        }
    }

    fun getLiteConfigDqIndex(): String {
        return when (this){
            MICRO -> "ToAy2018FT1P0069N008"
            COMPACT -> "ReCl2018FT1P0090N018"
            SEDAN -> "FoMo2018FT2P0150N007"
            SUV -> "ToRa2018FT1P0151N001"
            MINIVAN -> "CiGr2018FT2P0120N007"
            COMMERCIAL -> "ReMa2018FT2P0125N050"
            LUXURY -> "AuA62018FT2P0190N018"
            SPORT -> "AlA12018FT1P0252N001"
        }
    }
}