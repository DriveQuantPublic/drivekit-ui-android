package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.DetectionMode.*
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.enums.VehicleCategory
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleCategoryItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleTypeItem
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.DetectionModeType

fun Vehicle.computeTitle(context: Context, vehicles: List<Vehicle>): String {
    var defaultName = getDefaultTitle(this)
    if (this.name.equals(defaultName, true)){
        defaultName = "${context.getString(R.string.dk_vehicle_my_vehicle)} - ${getVehiclePositionInList(vehicles)}"
    } else {
        this.name?.let {
            defaultName = it
        }
    }
    return defaultName
}

fun Vehicle.computeSubtitle(context: Context, vehicles: List<Vehicle>): String? {
    val title = this.computeTitle(context, vehicles)
    var subtitle: String? = getDefaultTitle(this)

    if (this.liteConfig){
        VehicleTypeItem.CAR.getCategories(context).first { it.liteConfigDqIndex == this.dqIndex}.title?.let { categoryName ->
            subtitle = if (categoryName.equals(title, true)){
                null
            } else {
                categoryName
            }
        }
    }
    return subtitle
}

fun Vehicle.isConfigured(): Boolean {
    return when (this.detectionMode){
        DISABLED, GPS -> true
        BEACON -> this.beacon != null
        BLUETOOTH -> this.bluetooth != null
    }
}

fun Vehicle.getDeviceDisplayIdentifier(): String {
    return when (this.detectionMode){
        DISABLED, GPS -> ""
        BEACON -> this.beacon?.code ?: run { "" }
        BLUETOOTH -> this.bluetooth?.name ?: run { "" }
    }
}

fun Vehicle.getDetectionModeName(context: Context): String {
    return DetectionModeType.getEnumByDetectionMode(this.detectionMode).getTitle(context)
}

fun Vehicle.getCategoryName(context: Context): String? {
    val categories = VehicleTypeItem.CAR.getCategories(context)
    val matchedCategory = categories.first {
        VehicleCategory.getEnumByTypeIndex(this.typeIndex).name == it.category
    }
    return matchedCategory.title
}

fun Vehicle.getEngineTypeName(context: Context): String? {
    val engineIndexes = VehicleTypeItem.CAR.getEngineIndexes(context)
    val matchedEngineIndex = engineIndexes.first {
        VehicleEngineIndex.getEnumByValue(this.engineIndex) == it.engine
    }
    return matchedEngineIndex.title
}

// TODO: verify values
fun Vehicle.getGearBoxName(context: Context): String? {
    val identifier = when (this.gearboxIndex){
        1 -> "dk_gearbox_automatic"
        2 -> "dk_gearbox_manual_5"
        3 -> "dk_gearbox_manual_6"
        4 -> "dk_gearbox_manual_7"
        5 -> "dk_gearbox_manual_8"
        else -> null
    }
    return if (identifier == null){
        "N/A"
    } else {
        DKResource.convertToString(context, identifier)
    }
}

private fun Vehicle.getVehiclePositionInList(vehicles: List<Vehicle>): Int{
    return vehicles.indexOf(this) + 1
}

private fun getDefaultTitle(vehicle: Vehicle): String {
    return "${vehicle.brand} ${vehicle.model} ${vehicle.version}"
}