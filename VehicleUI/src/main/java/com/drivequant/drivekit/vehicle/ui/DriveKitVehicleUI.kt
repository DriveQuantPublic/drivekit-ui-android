package com.drivequant.drivekit.vehicle.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.listener.ContentMail
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.GetVehicleInfoByVehicleIdListener
import com.drivequant.drivekit.common.ui.navigation.VehicleUIEntryPoint
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.listener.VehiclePickerCompleteListener
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerVehicleListActivity
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryConfigType
import com.drivequant.drivekit.vehicle.ui.vehicledetail.activity.VehicleDetailActivity
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.GroupField
import com.drivequant.drivekit.vehicle.ui.vehicles.activity.VehiclesListActivity
import com.drivequant.drivekit.vehicle.ui.vehicles.fragment.VehiclesListFragment
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehicleAction
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehicleActionItem

object DriveKitVehicleUI : VehicleUIEntryPoint {
    internal const val TAG = "DriveKit Vehicle UI"

    internal var vehicleTypes: List<VehicleType> = listOf(VehicleType.CAR)
        private set
    internal var brands: List<VehicleBrand> = VehicleBrand.values().asList()
        private set
    internal var categoryConfigType: CategoryConfigType = CategoryConfigType.BOTH_CONFIG
        private set
    internal var vehicleEngineIndexes: List<VehicleEngineIndex> = VehicleEngineIndex.values().toList()
        private set
    internal var brandsWithIcons: Boolean = true
        private set

    internal var canAddVehicle: Boolean = true
        private set
    internal var canRemoveBeacon: Boolean = true
        private set
    internal var maxVehicles: Int? = null
        private set
    internal var hasOdometer: Boolean = false
        private set
    internal var vehicleActions: List<VehicleActionItem> = VehicleAction.values().toList()
        private set

    internal var detectionModes = DetectionMode.values().toList()
        private set
    private var userDetectionModes: List<DetectionMode>? = null
    internal var customFields: HashMap<GroupField, List<Field>> = hashMapOf()
        private set
    internal var beaconDiagnosticMail: ContentMail? = null
        private set
    internal var vehiclePickerComplete: VehiclePickerCompleteListener? = null

    private const val VEHICLE_ID_EXTRA = "vehicleId-extra"

    init {
        DriveKit.checkInitialization()
        DriveKitLog.i(TAG, "Initialization")
        DriveKitNavigationController.vehicleUIEntryPoint = this
    }

    fun initialize() {
        // Nothing to do currently.
    }

    fun configureVehiclesTypes(vehicleTypes: List<VehicleType>) {
        if (vehicleTypes.isNotEmpty()) {
            this.vehicleTypes = vehicleTypes
        } else {
            this.vehicleTypes = listOf(VehicleType.CAR)
        }
    }

    fun configureBrands(vehicleBrands: List<VehicleBrand>) {
        if (vehicleBrands.isNotEmpty()) {
            this.brands = vehicleBrands
        }
    }

    fun configureCategoryConfigType(categoryConfigType: CategoryConfigType) {
        this.categoryConfigType = categoryConfigType
    }

    fun configureEngineIndexes(vehicleEnginesIndex: List<VehicleEngineIndex>) {
        if (vehicleEnginesIndex.isNotEmpty()) {
            this.vehicleEngineIndexes = vehicleEnginesIndex
        }
    }

    fun showBrandsWithIcons(displayBrandsWithIcons: Boolean) {
        this.brandsWithIcons = displayBrandsWithIcons
    }

    fun enableAddVehicle(canAddVehicle: Boolean) {
        this.canAddVehicle = canAddVehicle
    }

    fun enableRemoveBeacon(canRemoveBeacon: Boolean) {
        this.canRemoveBeacon = canRemoveBeacon
    }

    fun configureMaxVehicles(maxVehicles: Int?) {
        val previousConfiguration = this.maxVehicles
        if (maxVehicles != null && maxVehicles >= 0) {
            this.maxVehicles = maxVehicles
        } else {
            this.maxVehicles = null
        }
        if (this.maxVehicles != previousConfiguration) {
            this.userDetectionModes?.let {
                configureDetectionModes(it)
            }
        }
    }

    fun configureVehicleActions(vehicleActions: List<VehicleActionItem>) {
        this.vehicleActions = vehicleActions
    }

    fun configureDetectionModes(detectionModes: List<DetectionMode>) {
        this.userDetectionModes = detectionModes
        val fixedModes: List<DetectionMode> = if (detectionModes.isEmpty()) {
            listOf(DetectionMode.DISABLED)
        } else {
            detectionModes.toSet().run {
                if (this.size == 1 && this.contains(DetectionMode.GPS) && (maxVehicles ?: 0) != 1) {
                    listOf(DetectionMode.GPS, DetectionMode.DISABLED)
                } else {
                    detectionModes
                }
            }
        }
        this.detectionModes = fixedModes
    }

    fun addCustomFieldsToGroup(groupField: GroupField, fieldsToAdd: List<Field>){
        this.customFields[groupField] = fieldsToAdd
    }

    fun configureBeaconDetailEmail(beaconDiagnosticMail: ContentMail){
        this.beaconDiagnosticMail = beaconDiagnosticMail
    }

    fun enableOdometer(hasOdometer: Boolean) {
        this.hasOdometer = hasOdometer
    }

    override fun startVehicleListActivity(context: Context) {
        VehiclesListActivity.launchActivity(context)
    }

    override fun createVehicleListFragment(): Fragment {
        return VehiclesListFragment()
    }

    override fun startVehicleDetailActivity(context: Context, vehicleId: String): Boolean {
        return DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()?.let { vehicle ->
            return if (vehicle.liteConfig) {
                false
            } else {
                val intent = Intent(context, VehicleDetailActivity::class.java)
                intent.putExtra(VEHICLE_ID_EXTRA, vehicleId)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                true
            }
        } ?: false
    }

    override fun getVehicleInfoById(context: Context, vehicleId: String, listener: GetVehicleInfoByVehicleIdListener) {
        DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()?.let {
            val vehicleName = it.buildFormattedName(context)
            listener.onVehicleInfoRetrieved(vehicleName, it.liteConfig)
        }?: run {
            Log.e("DriveKitVehicleUI", "Could not find vehicle with following vehicleId : $vehicleId")
        }
    }

    override fun getVehiclesFilterItems(context: Context): List<FilterItem> {
        val vehiclesFilterItems = mutableListOf<FilterItem>()
        val vehicles = VehicleUtils().fetchVehiclesOrderedByDisplayName(context)
        for (vehicle in vehicles) {
            val vehicleItem = object : FilterItem{
                override fun getItemId(): Any {
                    return vehicle.vehicleId
                }

                override fun getImage(context: Context): Drawable? {
                    return VehicleUtils().getVehicleDrawable(context, vehicle.vehicleId)
                }

                override fun getTitle(context: Context): String {
                    return vehicle.buildFormattedName(context)
                }
            }
            vehiclesFilterItems.add(vehicleItem)
        }
        return vehiclesFilterItems
    }

    @JvmOverloads
    fun startOdometerUIActivity(activity: Activity, vehicleId: String? = null) {
        OdometerVehicleListActivity.launchActivity(activity, vehicleId)
    }

    @JvmOverloads
    fun startOdometerUIActivity(context: Context, vehicleId: String? = null) {
        OdometerVehicleListActivity.launchActivity(context, vehicleId)
    }
}
