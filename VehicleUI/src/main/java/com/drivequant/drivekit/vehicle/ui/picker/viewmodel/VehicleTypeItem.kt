package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.enums.TruckType
import com.drivequant.drivekit.vehicle.enums.VehicleCategory
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.extension.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleBrandItem
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleCategoryItem
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleEngineItem
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleTruckTypeItem
import java.lang.IllegalArgumentException

enum class VehicleTypeItem(val vehicleType: VehicleType) {
    CAR(VehicleType.CAR),
    TRUCK(VehicleType.TRUCK);

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
            TRUCK -> DKResource.convertToString(context, "dk_vehicle_type_truck_title")
        }
    }

    fun getTruckTypes(context: Context): List<VehicleTruckTypeItem> {
        val truckTypes = mutableListOf<VehicleTruckTypeItem>()
        for (item in TruckType.values()){
            truckTypes.add(item.buildTruckTypeItem(context))
        }
        return truckTypes
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
                if (this.vehicleType == item.getVehicleType()) {
                    brands.add(item.buildBrandItem(context))
                }
            }
        }
        return brands
    }

    fun getEngineIndexes(context: Context): List<VehicleEngineItem> {
        val engineIndexes = mutableListOf<VehicleEngineItem>()
        for (item in DriveKitVehicleUI.vehicleEngineIndexes){
            if (item.getVehicleTypes().contains(this.vehicleType)) {
                engineIndexes.add(item.buildEngineIndexItem(context))
            }
        }
        return engineIndexes
    }
}