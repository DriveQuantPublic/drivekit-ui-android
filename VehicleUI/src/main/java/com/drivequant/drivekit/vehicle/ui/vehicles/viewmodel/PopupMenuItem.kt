package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.TextInputEditText
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicleManager
import com.drivequant.drivekit.vehicle.manager.VehicleDeleteQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleManagerStatus
import com.drivequant.drivekit.vehicle.manager.VehicleRenameQueryListener
import com.drivequant.drivekit.vehicle.ui.DriverVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.computeTitle
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import com.drivequant.drivekit.vehicle.ui.vehicledetail.activity.VehicleDetailActivity

enum class PopupMenuItem(
    private val descriptionIdentifier: String
) : MenuItem {
    SHOW("dk_vehicle_show"),
    RENAME("dk_vehicle_rename"),
    REPLACE("dk_vehicle_replace"),
    DELETE("dk_vehicle_delete");

    override fun getTitle(context: Context): String {
        val stringValue = DKResource.convertToString(context, descriptionIdentifier)
        return stringValue?.let { it }?: run { "" }
    }

    override fun isDisplayable(vehicle: Vehicle, vehicles: List<Vehicle>): Boolean {
        return when (this){
            SHOW -> !vehicle.liteConfig && DriverVehicleUI.displayVehicleDetail
            RENAME -> DriverVehicleUI.renameVehicle
            REPLACE -> DriverVehicleUI.replaceVehicle
            DELETE -> vehicles.size > 1 && DriverVehicleUI.deleteVehicle
        }
    }

    override fun onItemClicked(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle) {
        when (this) {
            SHOW -> manageShowVehicleDetail(context, vehicle)
            RENAME -> manageRenameVehicle(context, viewModel, vehicle)
            REPLACE -> manageReplaceVehicle(context, vehicle)
            DELETE -> manageDeleteVehicle(context, viewModel, vehicle)
        }
    }

    private fun manageShowVehicleDetail(context: Context, vehicle: Vehicle){
        VehicleDetailActivity.launchActivity(context, vehicle.vehicleId)
    }

    private fun manageReplaceVehicle(context: Context, vehicle: Vehicle){
        VehiclePickerActivity.launchActivity(context, vehicleToDelete = vehicle)
    }

    private fun manageRenameVehicle(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle) {
        var vehicleFieldInputEditText: TextInputEditText? = null
        val title = DKResource.convertToString(context, "dk_vehicle_rename_title")?.let { it }?: run { "" }
        val message = DKResource.convertToString(context, "dk_vehicle_rename_description")?.let { it }?: run { "" }
        val vehicleName = viewModel.getTitle(context, vehicle)

        val alert = DKAlertDialog.LayoutBuilder().init(context)
            .layout(R.layout.alert_dialog_vehicle_rename)
            .cancelable(true)
            .positiveButton(context.getString(R.string.dk_common_ok),
                DialogInterface.OnClickListener { _, _ ->
                    vehicleFieldInputEditText?.let {
                        DriveKitVehicleManager.renameVehicle(it.text.toString(), vehicle, object: VehicleRenameQueryListener {
                            override fun onResponse(status: VehicleManagerStatus) {
                                if (status == VehicleManagerStatus.SUCCESS){
                                    viewModel.fetchVehicles(SynchronizationType.CACHE)
                                }
                            }
                        })
                    }
                })
            .negativeButton(context.getString(R.string.dk_common_cancel),
                DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            .show()

        val titleTextView = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = alert.findViewById<TextView>(R.id.text_view_alert_description)
        vehicleFieldInputEditText = alert.findViewById(R.id.edit_text_field)

        titleTextView?.text = title
        titleTextView?.setBackgroundColor(DriveKitUI.colors.primaryColor())
        titleTextView?.headLine1(DriveKitUI.colors.fontColorOnPrimaryColor())

        descriptionTextView?.text = message
        descriptionTextView?.setTextColor(DriveKitUI.colors.mainFontColor())
        descriptionTextView?.normalText()

        vehicleFieldInputEditText?.setText(vehicleName)
    }

    private fun manageDeleteVehicle(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle){
        val title = DKResource.convertToString(context, "app_name")?.let { it }?: run { "" }
        val message = DKResource.buildString(context, "dk_vehicle_delete_confirm", vehicle.computeTitle(context, viewModel.vehiclesList))

        val alert = DKAlertDialog.LayoutBuilder().init(context)
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(true)
            .positiveButton(context.getString(R.string.dk_common_ok),
                DialogInterface.OnClickListener { _, _ ->
                    DriveKitVehicleManager.deleteVehicle(vehicle, object: VehicleDeleteQueryListener {
                        override fun onResponse(status: VehicleManagerStatus) {
                            if (status == VehicleManagerStatus.SUCCESS){
                                viewModel.fetchVehicles(SynchronizationType.CACHE)
                            }
                        }
                    })
                })
            .negativeButton(context.getString(R.string.dk_common_cancel),
                DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            .show()

        val titleTextView = alert.findViewById<TextView>(R.id.text_view__alert_title)
        val descriptionTextView = alert.findViewById<TextView>(R.id.text_view_alert_description)

        titleTextView?.text = title
        descriptionTextView?.text = message
        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }
}