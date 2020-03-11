package com.drivequant.drivekit.vehicle.ui

import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleCategory
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryConfigType


object DriverVehicleUI { // TODO add VehicleUIEntryPoint interface

    internal var vehicleTypes: List<VehicleType> = listOf(VehicleType.CAR)
    internal var vehicleCategories: List<VehicleCategory> = listOf(
        VehicleCategory.MICRO,
        VehicleCategory.COMPACT,
        VehicleCategory.SEDAN,
        VehicleCategory.SUV,
        VehicleCategory.MINIVAN,
        VehicleCategory.COMMERCIAL,
        VehicleCategory.LUXURY,
        VehicleCategory.SPORT
    )
    internal var vehicleEnginesIndex: List<VehicleEngineIndex> = listOf(
        VehicleEngineIndex.GASOLINE,
        VehicleEngineIndex.DIESEL,
        VehicleEngineIndex.ELECTRIC,
        VehicleEngineIndex.GASOLINE_HYBRID,
        VehicleEngineIndex.DIESEL_HYBRID
    )
    internal var vehicleBrands: List<VehicleBrand> = VehicleBrand.getBrands(null)

    internal var categoryConfigType: CategoryConfigType = CategoryConfigType.BOTH_CONFIG
    internal var displayBrandsWithIcons: Boolean = true

    internal var detectionModes: List<DetectionMode> = listOf(
        DetectionMode.DISABLED,
        DetectionMode.GPS,
        DetectionMode.BEACON,
        DetectionMode.BLUETOOTH
    )

    internal var maxVehicles: Int = -1
    internal var addVehicle: Boolean = true
    internal var replaceVehicle: Boolean = true
    internal var deleteVehicle: Boolean = true
    internal var renameVehicle: Boolean = true
    internal var displayVehicleDetail: Boolean = true

    fun initialize(vehicleTypes: List<VehicleType> = listOf(VehicleType.CAR),
                   maxVehicles: Int = -1,
                   categoryConfigType: CategoryConfigType = CategoryConfigType.BOTH_CONFIG,
                   detectionModes: List<DetectionMode> = listOf(
                       DetectionMode.DISABLED,
                       DetectionMode.GPS,
                       DetectionMode.BEACON,
                       DetectionMode.BLUETOOTH)) {

        this.vehicleTypes = vehicleTypes
        this.maxVehicles = maxVehicles
        this.categoryConfigType = categoryConfigType
        this.detectionModes = detectionModes
    }

    fun configureMaxVehicles(maxVehicles: Int){
        this.maxVehicles = maxVehicles
    }

    fun configureVehiclesTypes(vehicleTypes: List<VehicleType>){
        this.vehicleTypes = vehicleTypes
    }

    fun configureVehiclesCategories(vehicleCategories: List<VehicleCategory>){
        this.vehicleCategories = vehicleCategories
    }

    fun configureVehicleEnginesIndex(vehicleEnginesIndex: List<VehicleEngineIndex>){
        this.vehicleEnginesIndex = vehicleEnginesIndex
    }

    fun configureVehicleBrands(vehicleBrands: List<VehicleBrand>){
        this.vehicleBrands = vehicleBrands
    }

    fun configureDisplayBrandsWithIcons(displayBrandsWithIcons: Boolean){
        this.displayBrandsWithIcons = displayBrandsWithIcons
    }

    fun configureCategoryConfigType(categoryConfigType: CategoryConfigType){
        this.categoryConfigType = categoryConfigType
    }

    fun configureDetectionModes(detectionModes: List<DetectionMode>){
        this.detectionModes = detectionModes
    }

    fun configureAddVehicle(addVehicle: Boolean){
        this.addVehicle = addVehicle
    }

    fun configureReplaceVehicle(replaceVehicle: Boolean){
        this.replaceVehicle = replaceVehicle
    }

    fun configureDeleteVehicle(deleteVehicle: Boolean){
        this.deleteVehicle = deleteVehicle
    }

    fun configureRenameVehicle(renameVehicle: Boolean){
        this.renameVehicle = renameVehicle
    }

    fun displayVehicleDetail(displayVehicleDetail: Boolean){
        this.displayVehicleDetail = displayVehicleDetail
    }
}