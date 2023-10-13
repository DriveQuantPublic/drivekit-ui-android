package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex.*
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleEngineItem

fun VehicleEngineIndex.getTitle(context: Context) : String {
    val identifier = when (this){
        GASOLINE -> R.string.dk_vehicle_engine_gasoline
        DIESEL -> R.string.dk_vehicle_engine_diesel
        ELECTRIC -> R.string.dk_vehicle_engine_electric
        GASOLINE_HYBRID -> R.string.dk_vehicle_engine_gasoline_hybrid
        DIESEL_HYBRID -> R.string.dk_vehicle_engine_diesel_hybrid
        PLUG_IN_GASOLINE_HYBRID -> R.string.dk_vehicle_engine_gasoline_hybrid_plug_in
    }
    return context.getString(identifier)
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

fun VehicleEngineIndex.getVehicleTypes() : List<VehicleType> {
    val vehicleTypes = mutableListOf<VehicleType>()
    if (isCar) {
        vehicleTypes.add(VehicleType.CAR)
    }
    if (isTruck) {
        vehicleTypes.add(VehicleType.TRUCK)
    }
    return vehicleTypes
}