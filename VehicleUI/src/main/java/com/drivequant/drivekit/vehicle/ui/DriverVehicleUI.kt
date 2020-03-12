package com.drivequant.drivekit.vehicle.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.VehicleUIEntryPoint
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleCategory
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleItemListFragment
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryConfigType
import com.drivequant.drivekit.vehicle.ui.vehicles.activity.VehiclesListActivity
import com.drivequant.drivekit.vehicle.ui.vehicles.fragment.VehiclesListFragment
import java.io.Serializable


object DriverVehicleUI : VehicleUIEntryPoint {

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
        DriveKitNavigationController.vehicleUIEntryPoint = this
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

    override fun startVehicleListActivity(context: Context) {
        val intent = Intent(context, VehiclesListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun createVehicleListFragment(): Fragment = VehiclesListFragment()

    override fun startVehicleDetailActivity(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createVehicleDetailFragment(): Fragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createVehiclePickerActivity(context: Context) {
        val intent = Intent(context, VehiclePickerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun createVehiclePickerFragment(
        description: String,
        vehiclePickerStep: Int,
        items: Serializable): Fragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}