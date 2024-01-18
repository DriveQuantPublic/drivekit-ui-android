package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.vehicle.enums.TruckType
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleTruckTypeItem

fun TruckType.getTitle(context: Context): String {
    return when (this) {
        TruckType.STRAIGHT_TRUCK -> R.string.dk_vehicle_category_truck_straight
        TruckType.SEMI_TRAILER_TRUCK -> R.string.dk_vehicle_type_truck_tractor_semi_trailer
        TruckType.ROAD_TRAIN -> R.string.dk_vehicle_type_truck_road_train
    }.let { context.getString(it) }
}

fun TruckType.getIcon(context: Context): Drawable? {
    return when (this) {
        TruckType.STRAIGHT_TRUCK -> R.drawable.dk_icon_straight_truck
        TruckType.SEMI_TRAILER_TRUCK -> R.drawable.dk_icon_semi_trailer_truck
        TruckType.ROAD_TRAIN -> R.drawable.dk_icon_road_train
    }.let { ContextCompat.getDrawable(context, it) }
}

fun TruckType.buildTruckTypeItem(context: Context): VehicleTruckTypeItem {
    return VehicleTruckTypeItem(
        this.name,
        getTitle(context),
        getIcon(context)
    )
}
