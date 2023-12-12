package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.enums.VehicleCategory
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.COMMERCIAL
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.COMPACT
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.FOUR_AXLES_STRAIGHT_TRUCK
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.FOUR_AXLES_TRACTOR
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.LUXURY
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.MICRO
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.MINIVAN
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.SEDAN
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.SPORT
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.SUV
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.THREE_AXLES_STRAIGHT_TRUCK
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.THREE_AXLES_TRACTOR
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.TWO_AXLES_STRAIGHT_TRUCK
import com.drivequant.drivekit.vehicle.enums.VehicleCategory.TWO_AXLES_TRACTOR
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleCategoryItem

fun VehicleCategory.getTitle(context: Context): String {
    val identifier = when (this) {
        MICRO -> R.string.dk_vehicle_category_car_micro_title
        COMPACT -> R.string.dk_vehicle_category_car_compact_title
        SEDAN -> R.string.dk_vehicle_category_car_sedan_title
        SUV -> R.string.dk_vehicle_category_car_suv_title
        MINIVAN -> R.string.dk_vehicle_category_car_minivan_title
        COMMERCIAL -> R.string.dk_vehicle_category_car_commercial_title
        LUXURY -> R.string.dk_vehicle_category_car_luxury_title
        SPORT -> R.string.dk_vehicle_category_car_sport_title
        TWO_AXLES_STRAIGHT_TRUCK -> R.string.dk_vehicle_category_truck_straight_2_axles
        THREE_AXLES_STRAIGHT_TRUCK -> R.string.dk_vehicle_category_truck_straight_3_axles
        FOUR_AXLES_STRAIGHT_TRUCK -> R.string.dk_vehicle_category_truck_straight_4_axles
        TWO_AXLES_TRACTOR -> R.string.dk_vehicle_category_truck_trailer_2_axles
        THREE_AXLES_TRACTOR -> R.string.dk_vehicle_category_truck_trailer_3_axles
        FOUR_AXLES_TRACTOR -> R.string.dk_vehicle_category_truck_trailer_4_axles
    }
    return context.getString(identifier)
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
        TWO_AXLES_STRAIGHT_TRUCK,
        THREE_AXLES_STRAIGHT_TRUCK,
        FOUR_AXLES_STRAIGHT_TRUCK,
        TWO_AXLES_TRACTOR,
        THREE_AXLES_TRACTOR,
        FOUR_AXLES_TRACTOR -> ""
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
        TWO_AXLES_STRAIGHT_TRUCK,
        THREE_AXLES_STRAIGHT_TRUCK,
        FOUR_AXLES_STRAIGHT_TRUCK,
        TWO_AXLES_TRACTOR,
        THREE_AXLES_TRACTOR,
        FOUR_AXLES_TRACTOR -> ""
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
        TWO_AXLES_STRAIGHT_TRUCK,
        FOUR_AXLES_STRAIGHT_TRUCK,
        TWO_AXLES_TRACTOR,
        THREE_AXLES_TRACTOR,
        FOUR_AXLES_TRACTOR,
        THREE_AXLES_STRAIGHT_TRUCK -> ""
    }
    return DKResource.convertToString(context, identifier)
}

fun VehicleCategory.getLiteConfigDqIndex(engineIndex: VehicleEngineIndex): String {
    if (engineIndex == VehicleEngineIndex.ELECTRIC) {
         return when (this) {
             MICRO -> "ReTw2022FT3P0081N001"
             COMPACT -> "Fi502021FT3P0118N013"
             SEDAN -> "KiE-2020FT3P0204N004"
             SUV -> "TeMo2022FT3P0278N001"
             MINIVAN -> "OpZa2022FT3P0136N010"
             COMMERCIAL -> "PeEx2022FT3P0136N011"
             LUXURY -> "TeMo2021FT3P0282N002"
             SPORT -> "PoTa2022FT3P0000N018"
             TWO_AXLES_STRAIGHT_TRUCK,
             THREE_AXLES_STRAIGHT_TRUCK,
             FOUR_AXLES_STRAIGHT_TRUCK,
             TWO_AXLES_TRACTOR,
             THREE_AXLES_TRACTOR,
             FOUR_AXLES_TRACTOR -> ""
         }
    } else {
        return when (this) {
            MICRO -> "ToAy2018FT1P0069N008"
            COMPACT -> "ReCl2018FT1P0090N018"
            SEDAN -> "FoMo2018FT2P0150N007"
            SUV -> "ToRa2018FT1P0151N001"
            MINIVAN -> "CiGr2018FT2P0120N007"
            COMMERCIAL -> "ReMa2018FT2P0125N050"
            LUXURY -> "AuA62018FT2P0190N018"
            SPORT -> "AlA12018FT1P0252N001"
            TWO_AXLES_STRAIGHT_TRUCK,
            THREE_AXLES_STRAIGHT_TRUCK,
            FOUR_AXLES_STRAIGHT_TRUCK,
            TWO_AXLES_TRACTOR,
            THREE_AXLES_TRACTOR,
            FOUR_AXLES_TRACTOR -> ""
        }
    }
}

fun VehicleCategory.buildCategoryItem(context: Context): VehicleCategoryItem {
    return VehicleCategoryItem(
        this,
        this.name,
        getTitle(context),
        getIcon(context),
        getImage(context),
        getDescription(context),
        isCar,
        isMotorbike,
        isTruck
    )
}
