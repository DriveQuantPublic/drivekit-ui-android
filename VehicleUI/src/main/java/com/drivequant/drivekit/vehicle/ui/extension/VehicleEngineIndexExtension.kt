package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleEngineItem

fun VehicleEngineIndex.getTitle(context: Context) : String {
    val identifier = when (this){
        GASOLINE -> "dk_vehicle_engine_gasoline"
        DIESEL -> "dk_vehicle_engine_diesel"
        ELECTRIC -> "dk_vehicle_engine_electric"
        GASOLINE_HYBRID -> "dk_vehicle_engine_gasoline_hybrid"
        DIESEL_HYBRID -> "dk_vehicle_engine_diesel_hybrid"
    }
    return DKResource.convertToString(context, identifier)
}

fun VehicleEngineIndex.buildEngineIndexItem(context: Context) : VehicleEngineItem {
    return VehicleEngineItem(
        this,
        getTitle(context),
        isCar,
        isMotorbike,
        isTruck
    )
}