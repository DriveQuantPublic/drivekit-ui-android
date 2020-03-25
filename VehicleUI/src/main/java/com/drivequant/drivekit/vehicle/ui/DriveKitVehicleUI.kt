package com.drivequant.drivekit.vehicle.ui

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.VehicleUIEntryPoint
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconDiagnosticMail
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryConfigType
import com.drivequant.drivekit.vehicle.ui.vehicledetail.activity.VehicleDetailActivity
import com.drivequant.drivekit.vehicle.ui.vehicledetail.fragment.VehicleDetailFragment
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.GroupField
import com.drivequant.drivekit.vehicle.ui.vehicles.activity.VehiclesListActivity
import com.drivequant.drivekit.vehicle.ui.vehicles.fragment.VehiclesListFragment
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehicleAction

object DriveKitVehicleUI : VehicleUIEntryPoint {

    internal var vehicleTypes: List<VehicleType> = listOf(VehicleType.CAR)
    internal var brands: List<VehicleBrand> = VehicleBrand.getBrands(null)
    internal var categoryConfigType: CategoryConfigType = CategoryConfigType.BOTH_CONFIG
    internal var vehicleEngineIndexes: List<VehicleEngineIndex> = VehicleEngineIndex.values().toList()
    internal var brandsWithIcons: Boolean = true

    internal var canAddVehicle: Boolean = true
    internal var canRemoveBeacon: Boolean = true
    internal var maxVehicles: Int? = null
    internal var vehicleActions: List<VehicleAction> = VehicleAction.values().toList()

    internal var detectionModes: List<DetectionMode> = listOf(
        DetectionMode.DISABLED,
        DetectionMode.GPS,
        DetectionMode.BEACON,
        DetectionMode.BLUETOOTH
    )

    internal var customFields: HashMap<GroupField, List<Field>> = hashMapOf()
    internal var beaconDiagnosticMail: BeaconDiagnosticMail? = null

    private const val VEHICLE_ID_EXTRA = "vehicleId-extra"

    fun initialize(vehicleTypes: List<VehicleType> = listOf(VehicleType.CAR),
                   maxVehicles: Int? = null,
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

    fun configureVehiclesTypes(vehicleTypes: List<VehicleType>){
        if (vehicleTypes.isNotEmpty()) {
            this.vehicleTypes = vehicleTypes
        }
    }

    fun configureBrands(vehicleBrands: List<VehicleBrand>){
        if (vehicleBrands.isNotEmpty()) {
            this.brands = vehicleBrands
        }
    }

    fun configureCategoryConfigType(categoryConfigType: CategoryConfigType){
        this.categoryConfigType = categoryConfigType
    }

    fun configureEngineIndexes(vehicleEnginesIndex: List<VehicleEngineIndex>){
        this.vehicleEngineIndexes = vehicleEnginesIndex
    }

    fun showBrandsWithIcons(displayBrandsWithIcons: Boolean){
        this.brandsWithIcons = displayBrandsWithIcons
    }

    fun enableAddVehicle(canAddVehicle: Boolean){
        this.canAddVehicle = canAddVehicle
    }

    fun enableRemoveBeacon(canRemoveBeacon: Boolean){
        this.canRemoveBeacon = canRemoveBeacon
    }

    fun configureMaxVehicles(maxVehicles: Int?){
        if (maxVehicles != null && maxVehicles >= 0) {
            this.maxVehicles = maxVehicles
        }
    }

    fun configureVehicleActions(vehicleActions: List<VehicleAction>){
        this.vehicleActions = vehicleActions
    }

    fun configureDetectionModes(detectionModes: List<DetectionMode>){
        if (detectionModes.isNotEmpty()) {
            this.detectionModes = detectionModes
        }
    }

    fun addCustomFieldsToGroup(groupField: GroupField, fieldsToAdd: List<Field>){
        this.customFields[groupField] = fieldsToAdd
    }

    fun configureBeaconDetailEmail(beaconDiagnosticMail: BeaconDiagnosticMail){
        this.beaconDiagnosticMail = beaconDiagnosticMail
    }

    override fun startVehicleListActivity(context: Context) {
        val intent = Intent(context, VehiclesListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun createVehicleListFragment(): Fragment = VehiclesListFragment()

    override fun createVehiclePickerActivity(context: Context) {
        val intent = Intent(context, VehiclePickerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun createVehicleDetailFragment(vehicleId: String): Fragment = VehicleDetailFragment.newInstance(vehicleId)

    override fun createVehicleDetailActivity(context: Context, vehicleId: String) {
        val intent = Intent(context, VehicleDetailActivity::class.java)
        intent.putExtra(VEHICLE_ID_EXTRA,vehicleId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}