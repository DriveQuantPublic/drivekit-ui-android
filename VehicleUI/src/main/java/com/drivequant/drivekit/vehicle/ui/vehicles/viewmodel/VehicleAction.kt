package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.TextInputEditText
import android.widget.TextView
import android.widget.Toast
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleDeleteQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleManagerStatus
import com.drivequant.drivekit.vehicle.manager.VehicleRenameQueryListener
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity

enum class VehicleAction(
    private val descriptionIdentifier: String
) : VehicleActionItem {
    SHOW("dk_vehicle_show"),
    RENAME("dk_vehicle_rename"),
    REPLACE("dk_vehicle_replace"),
    DELETE("dk_vehicle_delete");

    override fun getTitle(context: Context): String {
        return DKResource.convertToString(context, descriptionIdentifier)
    }

    override fun isDisplayable(vehicle: Vehicle): Boolean {
        return when (this){
            SHOW -> !vehicle.liteConfig && DriveKitVehicleUI.vehicleActions.contains(SHOW)
            RENAME -> DriveKitVehicleUI.vehicleActions.contains(RENAME)
            REPLACE -> DriveKitVehicleUI.vehicleActions.contains(REPLACE)
            DELETE -> DriveKitVehicle.vehiclesQuery().noFilter().query().execute().size > 1
                    && DriveKitVehicleUI.vehicleActions.contains(DELETE)
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
        DriveKitNavigationController.vehicleUIEntryPoint?.startVehicleDetailActivity(context, vehicle.vehicleId)
    }

    private fun manageReplaceVehicle(context: Context, vehicle: Vehicle){
        VehiclePickerActivity.launchActivity(context, vehicleToDelete = vehicle)
    }

    private fun manageRenameVehicle(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle) {
        var vehicleFieldInputEditText: TextInputEditText? = null
        val title = DKResource.convertToString(context, "dk_vehicle_rename_title")
        val message = DKResource.convertToString(context, "dk_vehicle_rename_description")
        val vehicleName = viewModel.getTitle(context, vehicle)

        val alert = DKAlertDialog.LayoutBuilder().init(context)
            .layout(R.layout.alert_dialog_vehicle_rename)
            .cancelable(true)
            .positiveButton(context.getString(R.string.dk_common_ok),
                DialogInterface.OnClickListener { _, _ ->
                    vehicleFieldInputEditText?.let {
                        viewModel.progressBarObserver.postValue(true)
                        DriveKitVehicle.renameVehicle(it.text.toString(), vehicle, object: VehicleRenameQueryListener {
                            override fun onResponse(status: VehicleManagerStatus) {
                                viewModel.progressBarObserver.postValue(false)
                                if (status == VehicleManagerStatus.SUCCESS){
                                    viewModel.fetchVehicles(context)
                                } else {
                                    displayError(context)
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
        val title = DKResource.convertToString(context, "app_name")
        val vehicleName = vehicle.buildFormattedName(context)
        val message = DKResource.buildString(context, "dk_vehicle_delete_confirm", vehicleName)
        val alert = DKAlertDialog.LayoutBuilder().init(context)
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(true)
            .positiveButton(context.getString(R.string.dk_common_ok),
                DialogInterface.OnClickListener { _, _ ->
                    DriveKitVehicle.deleteVehicle(vehicle, object: VehicleDeleteQueryListener {
                        override fun onResponse(status: VehicleManagerStatus) {
                            if (status == VehicleManagerStatus.SUCCESS){
                                viewModel.fetchVehicles(context)
                            } else {
                                displayError(context)
                            }
                        }
                    })
                })
            .negativeButton(context.getString(R.string.dk_common_cancel),
                DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            .show()

        val titleTextView = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = alert.findViewById<TextView>(R.id.text_view_alert_description)

        titleTextView?.text = title
        descriptionTextView?.text = message
        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }

    private fun displayError(context: Context){
        Toast.makeText(context, DKResource.convertToString(context, "dk_vehicle_error_message"), Toast.LENGTH_SHORT).show()
    }
}