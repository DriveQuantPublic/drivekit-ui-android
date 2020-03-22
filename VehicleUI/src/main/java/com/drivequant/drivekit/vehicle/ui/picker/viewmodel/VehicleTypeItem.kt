package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKMedia
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import java.lang.IllegalArgumentException

enum class VehicleTypeItem(
    val vehicleType: VehicleType,
    private val titleStringResId: Int
) {
    CAR(VehicleType.CAR, R.string.dk_vehicle_type_car_title);

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
        return context.getString(this.titleStringResId)
    }

    fun getCategories(context: Context): List<VehicleCategoryItem> {
        val lines = DKMedia.readCSVFile(context, R.raw.vehicle_categories)
        val categories = mutableListOf<VehicleCategoryItem>()
        for (i in lines!!.indices) {
            val vehicleCategoryItem = buildVehicleCategoryItem(context, lines[i])
            categories.add(vehicleCategoryItem)
        }
        return when (this){
            CAR -> categories.filter { it.isCar}
        }
    }

    fun getBrands(context: Context, withIcons: Boolean = false): List<VehicleBrandItem> {
        val lines = DKMedia.readCSVFile(context, R.raw.vehicle_brands)
        val brands = mutableListOf<VehicleBrandItem>()
        for (i in lines!!.indices) {
            val vehicleBrandItem = buildVehicleBrandItem(context, lines[i])
            if (!withIcons || (withIcons && vehicleBrandItem.icon != null)){
                if (DriveKitVehicleUI.brands.contains(vehicleBrandItem.brand)) {
                    brands.add(vehicleBrandItem)
                }
            }
        }
        return when (this){
            CAR -> brands.filter { it.isCar}
        }
    }

    fun getEngineIndexes(context: Context): List<VehicleEngineItem> {
        val lines = DKMedia.readCSVFile(context, R.raw.vehicle_engines)
        val engines = mutableListOf<VehicleEngineItem>()
        for (i in lines!!.indices) {
            val vehicleEngineItem = buildVehicleEngineItem(context, lines[i])
            if (DriveKitVehicleUI.vehicleEngineIndexes.contains(vehicleEngineItem.engine)) {
                engines.add(vehicleEngineItem)
            }
        }
        return when (this){
            CAR -> engines.filter { it.isCar}
        }
    }
}