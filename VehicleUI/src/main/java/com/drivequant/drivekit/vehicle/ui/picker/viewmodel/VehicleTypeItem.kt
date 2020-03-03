package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import com.drivequant.drivekit.vehicle.enum.VehicleType
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.commons.MediaUtils

enum class VehicleTypeItem(
    val vehicleType: VehicleType,
    private val titleStringResId: Int
) {
    CAR(VehicleType.CAR, R.string.dk_vehicle_type_car_title),
    MOTORBIKE(VehicleType.MOTORBIKE, R.string.dk_vehicle_type_motorbike_title),
    TRUCK(VehicleType.TRUCK, R.string.dk_vehicle_type_truck_title);

    fun getTitle(context: Context): String {
        return context.getString(this.titleStringResId)
    }

    fun getCategories(context: Context): List<VehicleCategoryItem> {
        val lines = MediaUtils.readCSVFile(context, R.raw.vehicle_categories)
        val categories = mutableListOf<VehicleCategoryItem>()
        for (i in lines!!.indices) {
            val vehicleCategoryItem = buildVehicleCategoryItem(context, lines[i])
            categories.add(vehicleCategoryItem)
        }
        return when (this){
            CAR -> categories.filter { it.isCar}
            MOTORBIKE -> categories.filter { it.isMotorbike}
            TRUCK -> categories.filter { it.isTruck}
        }
    }

    fun getBrands(context: Context, withIcons: Boolean = false): List<VehicleBrandItem> {
        val lines = MediaUtils.readCSVFile(context, R.raw.vehicle_brands)
        val brands = mutableListOf<VehicleBrandItem>()
        for (i in lines!!.indices) {
            val vehicleBrandItem = buildVehicleBrandItem(context, lines[i])
            if (!withIcons || (withIcons && vehicleBrandItem.icon != null)){
                brands.add(vehicleBrandItem)
            }
        }
        return when (this){
            CAR -> brands.filter { it.isCar}
            MOTORBIKE -> brands.filter { it.isMotorbike}
            TRUCK -> brands.filter { it.isTruck}
        }
    }

    fun getEngines(context: Context): List<VehicleEngineItem> {
        val lines = MediaUtils.readCSVFile(context, R.raw.vehicle_engines)
        val engines = mutableListOf<VehicleEngineItem>()
        for (i in lines!!.indices) {
            val vehicleEngineItem = buildVehicleEngineItem(context, lines[i])
            engines.add(vehicleEngineItem)
        }
        return when (this){
            CAR -> engines.filter { it.isCar}
            MOTORBIKE -> engines.filter { it.isMotorbike}
            TRUCK -> engines.filter { it.isTruck}
        }
    }
}