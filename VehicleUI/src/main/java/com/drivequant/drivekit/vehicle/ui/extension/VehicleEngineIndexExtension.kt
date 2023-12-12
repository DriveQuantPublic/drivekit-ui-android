package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex.*
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleEngineItem

fun VehicleEngineIndex.getTitle(context: Context): String =
    when (this) {
        GASOLINE -> R.string.dk_vehicle_engine_gasoline
        DIESEL -> R.string.dk_vehicle_engine_diesel
        ELECTRIC -> R.string.dk_vehicle_engine_electric
        GASOLINE_HYBRID -> R.string.dk_vehicle_engine_gasoline_hybrid
        DIESEL_HYBRID -> R.string.dk_vehicle_engine_diesel_hybrid
        BIOFUEL -> R.string.dk_vehicle_engine_biofuel
        BI_FUEL_BIOETHANOL -> R.string.dk_vehicle_engine_bi_fuel_bioethanol
        BI_FUEL_NGV -> R.string.dk_vehicle_engine_bi_fuel_ngv
        BI_FUEL_LPG -> R.string.dk_vehicle_engine_bi_fuel_lpg
        NOT_AVAILABLE -> null
        PLUG_IN_GASOLINE_HYBRID -> R.string.dk_vehicle_engine_gasoline_hybrid_plug_in
        HYDROGEN -> R.string.dk_vehicle_engine_hydrogen
    }?.let {
        context.getString(it)
    } ?: run {
        "-"
    }

fun VehicleEngineIndex.buildEngineIndexItem(context: Context): VehicleEngineItem {
    return VehicleEngineItem(
        this,
        getTitle(context),
        isCar,
        isMotorbike,
        isTruck
    )
}