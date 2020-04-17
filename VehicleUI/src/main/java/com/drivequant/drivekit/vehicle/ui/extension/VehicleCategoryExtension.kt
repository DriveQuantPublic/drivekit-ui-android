package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.enums.VehicleCategory
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleCategoryItem

fun VehicleCategory.getTitle(context: Context): String {
    val identifier = when (this){
        MICRO -> "dk_vehicle_category_car_micro_title"
        COMPACT -> "dk_vehicle_category_car_compact_title"
        SEDAN -> "dk_vehicle_category_car_sedan_title"
        SUV -> "dk_vehicle_category_car_suv_title"
        MINIVAN -> "dk_vehicle_category_car_minivan_title"
        COMMERCIAL -> "dk_vehicle_category_car_commercial_title"
        LUXURY -> "dk_vehicle_category_car_luxury_title"
        SPORT -> "dk_vehicle_category_car_sport_title"
    }
    return DKResource.convertToString(context, identifier)
}

fun VehicleCategory.getIcon(context: Context): Drawable? {
    val identifier = when (this){
        MICRO -> "dk_icon_micro"
        COMPACT -> "dk_icon_compact"
        SEDAN -> "dk_icon_sedan"
        SUV -> "dk_icon_suv"
        MINIVAN -> "dk_icon_minivan"
        COMMERCIAL -> "dk_icon_commercial"
        LUXURY -> "dk_icon_luxury"
        SPORT -> "dk_icon_sport"
    }
    return DKResource.convertToDrawable(context, identifier)
}

fun VehicleCategory.getImage(context: Context): Drawable? {
    val identifier = when (this){
        MICRO -> "dk_image_micro"
        COMPACT -> "dk_image_compact"
        SEDAN -> "dk_image_sedan"
        SUV -> "dk_image_suv"
        MINIVAN -> "dk_image_minivan"
        COMMERCIAL -> "dk_image_commercial"
        LUXURY -> "dk_image_luxury"
        SPORT -> "dk_image_sport"
    }
    return DKResource.convertToDrawable(context, identifier)
}

fun VehicleCategory.getDescription(context: Context): String {
    val identifier = when (this){
        MICRO -> "dk_vehicle_category_car_micro_description"
        COMPACT -> "dk_vehicle_category_car_compact_description"
        SEDAN -> "dk_vehicle_category_car_sedan_description"
        SUV -> "dk_vehicle_category_car_suv_description"
        MINIVAN -> "dk_vehicle_category_car_minivan_description"
        COMMERCIAL -> "dk_vehicle_category_car_commercial_description"
        LUXURY -> "dk_vehicle_category_car_luxury_description"
        SPORT -> "dk_vehicle_category_car_sport_description"
    }
    return DKResource.convertToString(context, identifier)
}

fun VehicleCategory.getLiteConfigDqIndex(): String {
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

fun VehicleCategory.buildCategoryItem(context: Context): VehicleCategoryItem {
    return VehicleCategoryItem(
        this.name,
        getTitle(context),
        getIcon(context),
        getImage(context),
        getDescription(context),
        getLiteConfigDqIndex(),
        isCar,
        isMotorbike,
        isTruck
    )
}