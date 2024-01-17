package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import android.text.TextUtils
import com.drivequant.drivekit.databaseutils.entity.DetectionMode.BEACON
import com.drivequant.drivekit.databaseutils.entity.DetectionMode.BLUETOOTH
import com.drivequant.drivekit.databaseutils.entity.DetectionMode.DISABLED
import com.drivequant.drivekit.databaseutils.entity.DetectionMode.GPS
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.enums.VehicleCategory
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleTypeItem
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils

fun Vehicle.buildFormattedName(context: Context) : String {
    val sortedVehicles = VehicleUtils().fetchVehiclesOrderedByDisplayName(context)
    return if (!TextUtils.isEmpty(name) && !VehicleUtils().isNameEqualsDefaultName(this)) {
        name ?: " "
    } else {
        val vehiclePositionInList = sortedVehicles.indexOf(this) + 1
        val myVehicleString = context.getString(R.string.dk_vehicle_my_vehicle)
        "$myVehicleString - $vehiclePositionInList"
    }
}

fun Vehicle.computeSubtitle(context: Context): String? {
    val title = this.buildFormattedName(context)
    var subtitle: String? = "$brand $model $version"
    if (VehicleType.getVehicleType(this.typeIndex) == VehicleType.CAR && liteConfig) {
        VehicleCategory.getEnumByTypeIndex(this.typeIndex)?.getTitle(context)?.let { categoryName ->
            subtitle = if (categoryName.equals(title, true)) {
                null
            } else {
                categoryName
            }
        }
    }
    return subtitle
}

fun Vehicle.isConfigured(): Boolean {
    return when (detectionMode) {
        DISABLED, GPS -> true
        BEACON -> beacon != null
        BLUETOOTH -> bluetooth != null
    }
}

fun Vehicle.getDeviceDisplayIdentifier(): String {
    return when (detectionMode) {
        DISABLED, GPS -> ""
        BEACON -> beacon?.code ?: ""
        BLUETOOTH -> bluetooth?.name ?: ""
    }
}

fun Vehicle.getCategoryName(context: Context): String {
    VehicleType.getVehicleType(typeIndex)?.let { vehicleType ->
        return VehicleCategory.getEnumByTypeIndex(typeIndex)?.let { vehicleCategory ->
            val categories = VehicleTypeItem.getEnumByVehicleType(vehicleType).getCategories(context)
            val matchedCategory = categories.first {
                vehicleCategory.name == it.category
            }
            matchedCategory.title
        } ?: run { "-" }
    }?:run {
        return "-"
    }
}

fun Vehicle.getEngineTypeName(context: Context): String
    = VehicleEngineIndex.getEnumByValue(this.engineIndex).getTitle(context)

fun Vehicle.getGearBoxName(context: Context): String {
    val identifier = when (gearboxIndex) {
        1 -> R.string.dk_gearbox_automatic
        2 -> R.string.dk_gearbox_manual_5
        3 -> R.string.dk_gearbox_manual_6
        4 -> R.string.dk_gearbox_manual_7
        5 -> R.string.dk_gearbox_manual_8
        else -> com.drivequant.drivekit.common.ui.R.string.dk_common_no_value
    }
    return context.getString(identifier)
}

fun Vehicle.getDefaultImage(): Int {
    var drawableId: Int? = null
    VehicleType.getVehicleType(typeIndex)?.let { vehicleType ->
        drawableId = when (vehicleType) {
            VehicleType.CAR -> R.drawable.dk_default_car
            VehicleType.TRUCK -> R.drawable.dk_default_truck
        }
    }
    drawableId?.let {
        return it
    }?:run {
        return R.drawable.dk_default_car
    }
}
