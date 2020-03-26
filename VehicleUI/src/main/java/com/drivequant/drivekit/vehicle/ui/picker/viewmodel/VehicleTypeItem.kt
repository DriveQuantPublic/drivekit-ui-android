package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.enums.VehicleCategory
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.extension.buildBrandItem
import com.drivequant.drivekit.vehicle.ui.extension.buildCategoryItem
import com.drivequant.drivekit.vehicle.ui.extension.buildEngineIndexItem
import com.drivequant.drivekit.vehicle.ui.extension.hasIcon
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleBrandItem
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleCategoryItem
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleEngineItem
import java.lang.IllegalArgumentException

enum class VehicleTypeItem(val vehicleType: VehicleType) {
    CAR(VehicleType.CAR);

    companion object {
        fun getEnumByVehicleType(vehicleType: VehicleType): VehicleTypeItem{
            for (x in values()){
                if (x.vehicleType == vehicleType){
                    return x
                }
            }
            throw IllegalArgumentException("Value $vehicleType not found in VehicleTypeItem@ list")
        }
    }

    fun getTitle(context: Context): String {
        return when (this){
            CAR -> DKResource.convertToString(context, "dk_vehicle_type_car_title")
        }
    }

    fun getCategories(context: Context): List<VehicleCategoryItem> {
        val categories = mutableListOf<VehicleCategoryItem>()
        for (item in VehicleCategory.getCategories(vehicleType)){
            categories.add(item.buildCategoryItem(context))
        }
        return categories
    }

    fun getBrands(context: Context, withIcons: Boolean = false): List<VehicleBrandItem> {
        val brands = mutableListOf<VehicleBrandItem>()
        for (item in DriveKitVehicleUI.brands) {
            if ((withIcons && item.hasIcon(context)) || !withIcons) {
                brands.add(item.buildBrandItem(context))
            }
        }
        return brands
    }

    fun getEngineIndexes(context: Context): List<VehicleEngineItem> {
        val engineIndexes = mutableListOf<VehicleEngineItem>()
        for (item in DriveKitVehicleUI.vehicleEngineIndexes){
            engineIndexes.add(item.buildEngineIndexItem(context))
        }
        return engineIndexes
    }
}