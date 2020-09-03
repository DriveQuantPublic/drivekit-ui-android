package com.drivequant.drivekit.vehicle.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.listener.ContentMail
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.GetVehicleInfoByVehicleIdListener
import com.drivequant.drivekit.common.ui.navigation.GetVehiclesFilterItems
import com.drivequant.drivekit.common.ui.navigation.VehicleUIEntryPoint
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.listener.VehiclePickerExtraStepListener
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryConfigType
import com.drivequant.drivekit.vehicle.ui.vehicledetail.activity.VehicleDetailActivity
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.GroupField
import com.drivequant.drivekit.vehicle.ui.vehicles.activity.VehiclesListActivity
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehicleAction
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehicleActionItem

object DriveKitVehicleUI : VehicleUIEntryPoint {

    internal var vehicleTypes: List<VehicleType> = VehicleType.values().asList()
    internal var brands: List<VehicleBrand> = VehicleBrand.values().asList()
    internal var categoryConfigType: CategoryConfigType = CategoryConfigType.BOTH_CONFIG
    internal var vehicleEngineIndexes: List<VehicleEngineIndex> = VehicleEngineIndex.values().toList()
    internal var brandsWithIcons: Boolean = true

    internal var canAddVehicle: Boolean = true
    internal var canRemoveBeacon: Boolean = true
    internal var maxVehicles: Int? = null
    internal var vehicleActions: List<VehicleActionItem> = VehicleAction.values().toList()

    internal var detectionModes: List<DetectionMode> = listOf(
        DetectionMode.DISABLED,
        DetectionMode.GPS,
        DetectionMode.BEACON,
        DetectionMode.BLUETOOTH
    )
    internal var customFields: HashMap<GroupField, List<Field>> = hashMapOf()
    internal var beaconDiagnosticMail: ContentMail? = null
    internal var vehiclePickerExtraStep: VehiclePickerExtraStepListener? = null

    private const val VEHICLE_ID_EXTRA = "vehicleId-extra"

    @JvmOverloads
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
        if (vehicleEnginesIndex.isNotEmpty()) {
            this.vehicleEngineIndexes = vehicleEnginesIndex
        }
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

    fun configureVehicleActions(vehicleActions: List<VehicleActionItem>){
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

    fun configureBeaconDetailEmail(beaconDiagnosticMail: ContentMail){
        this.beaconDiagnosticMail = beaconDiagnosticMail
    }

    fun configureVehiclePickerExtraStep(listener: VehiclePickerExtraStepListener) {
        this.vehiclePickerExtraStep = listener
    }

    override fun startVehicleListActivity(context: Context) {
        val intent = Intent(context, VehiclesListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun startVehicleDetailActivity(context: Context, vehicleId: String) : Boolean {
        DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()?.let { vehicle ->
            return if (vehicle.liteConfig){
                false
            } else {
                val intent = Intent(context, VehicleDetailActivity::class.java)
                intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                true
            }
        }?: run {
            return false
        }
    }

    override fun getVehicleInfoById(context: Context, vehicleId: String, listener: GetVehicleInfoByVehicleIdListener) {
        DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()?.let {
            val vehicleName = it.buildFormattedName(context)
            listener.onVehicleInfoRetrieved(vehicleName, it.liteConfig)
        }?: run {
            Log.e("DriveKitVehicleUI", "Could not find vehicle with following vehicleId : $vehicleId")
        }
    }

    override fun getVehiclesFilterItems(context: Context, listener: GetVehiclesFilterItems) {
        val vehiclesFilterItems = mutableListOf<FilterItem>()
        val vehicles = VehicleUtils().fetchVehiclesOrderedByDisplayName(context)
        val newVehicleList = mutableListOf<Vehicle?>()
        newVehicleList.add(0, null)
        for ((index,vehicle) in vehicles.withIndex()) {
            newVehicleList.add(index+1, vehicle)
        }

        if (newVehicleList.isNotEmpty()) {
            for (vehicle in newVehicleList) {
                val title = vehicle?.buildFormattedName(context) ?: kotlin.run {
                    DKResource.convertToString(context, "dk_driverdata_default_filter_option")
                }

                vehiclesFilterItems.add(
                    FilterItem(
                        vehicle?.vehicleId,
                        VehicleUtils().getVehicleDrawable(context, vehicle?.vehicleId),
                        title
                    )
                )
            }
            listener.onFilterItemsReceived(vehiclesFilterItems)
        }
    }
}