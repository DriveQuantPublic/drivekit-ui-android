package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.enums.TruckType
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleTruckTypeItem

fun TruckType.getTitle(context: Context): String {
    val identifier = when (this){
        TruckType.STRAIGHT_TRUCK -> "dk_vehicle_category_truck_straight"
        TruckType.SEMI_TRAILER_TRUCK -> "dk_vehicle_type_truck_tractor_semi_trailer"
        TruckType.ROAD_TRAIN -> "dk_vehicle_type_truck_road_train"
    }
    return DKResource.convertToString(context, identifier)
}

fun TruckType.getIcon(context: Context): Drawable? {
    val identifier = when (this){
        TruckType.STRAIGHT_TRUCK -> "dk_icon_straight_truck"
        TruckType.SEMI_TRAILER_TRUCK -> "dk_icon_semi_trailer_truck"
        TruckType.ROAD_TRAIN -> "dk_icon_road_train"
    }
    return DKResource.convertToDrawable(context, identifier)
}

fun TruckType.buildTruckTypeItem(context: Context): VehicleTruckTypeItem {
    return VehicleTruckTypeItem(
        this.name,
        getTitle(context),
        getIcon(context)
    )
}