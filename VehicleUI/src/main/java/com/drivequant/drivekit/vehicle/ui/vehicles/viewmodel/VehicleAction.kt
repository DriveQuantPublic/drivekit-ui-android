package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.app.Activity
import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.graphical.DKColors
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
import com.google.android.material.textfield.TextInputEditText

enum class VehicleAction(
    @StringRes private val descriptionIdentifier: Int
) : VehicleActionItem {
    SHOW(R.string.dk_vehicle_show),
    RENAME(R.string.dk_vehicle_rename),
    REPLACE(R.string.dk_vehicle_replace),
    DELETE(R.string.dk_vehicle_delete),
    ODOMETER(R.string.dk_vehicle_odometer);

    override fun getTitle(context: Context): String = context.getString(descriptionIdentifier)

    override fun isDisplayable(vehicle: Vehicle) = when (this) {
        SHOW -> !vehicle.liteConfig && DriveKitVehicleUI.vehicleActions.contains(SHOW)
        RENAME -> DriveKitVehicleUI.vehicleActions.contains(RENAME)
        REPLACE -> DriveKitVehicleUI.vehicleActions.contains(REPLACE)
        DELETE -> DriveKitVehicle.vehiclesQuery().noFilter().countQuery().execute() > 1
                && DriveKitVehicleUI.vehicleActions.contains(DELETE)
        ODOMETER -> DriveKitVehicleUI.hasOdometer && DriveKitVehicleUI.vehicleActions.contains(ODOMETER)
    }

    override fun onItemClicked(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle) {
        when (this) {
            SHOW -> manageShowVehicleDetail(context, vehicle)
            RENAME -> manageRenameVehicle(context, viewModel, vehicle)
            REPLACE -> manageReplaceVehicle(context, vehicle)
            DELETE -> manageDeleteVehicle(context, viewModel, vehicle)
            ODOMETER -> manageShowOdometer(context, vehicle)
        }
    }

    private fun manageShowOdometer(context: Context, vehicle: Vehicle) {
        DriveKitVehicleUI.startOdometerUIActivity(context as Activity, vehicle.vehicleId)
    }

    private fun manageShowVehicleDetail(context: Context, vehicle: Vehicle) {
        DriveKitNavigationController.vehicleUIEntryPoint?.startVehicleDetailActivity(
            context,
            vehicle.vehicleId
        )
    }

    private fun manageReplaceVehicle(context: Context, vehicle: Vehicle) {
        VehiclePickerActivity.launchActivity(context, vehicleToDelete = vehicle)
    }

    private fun manageRenameVehicle(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle) {
        var vehicleFieldInputEditText: TextInputEditText? = null
        val vehicleName = viewModel.getTitle(context, vehicle)

        val alert = DKAlertDialog.LayoutBuilder().init(context)
            .layout(R.layout.alert_dialog_vehicle_rename)
            .cancelable(true)
            .positiveButton(positiveListener = { _, _ ->
                vehicleFieldInputEditText?.let {
                    viewModel.progressBarObserver.postValue(true)
                    DriveKitVehicle.renameVehicle(it.text.toString(), vehicle, object: VehicleRenameQueryListener {
                        override fun onResponse(status: VehicleManagerStatus) {
                            viewModel.progressBarObserver.postValue(false)
                            if (status == VehicleManagerStatus.SUCCESS) {
                                viewModel.fetchVehicles(context)
                            } else {
                                displayError(context)
                            }
                        }
                    })
                }
            })
            .negativeButton()
            .show()

        val titleTextView = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = alert.findViewById<TextView>(R.id.text_view_alert_description)
        vehicleFieldInputEditText = alert.findViewById(R.id.edit_text_field)

        titleTextView?.setText(R.string.dk_vehicle_rename_title)
        descriptionTextView?.setText(R.string.dk_vehicle_rename_description)

        vehicleFieldInputEditText?.apply {
            setText(vehicleName)
            setTypeface(DriveKitUI.primaryFont(context), typeface.style)
        }
    }

    private fun manageDeleteVehicle(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle) {
        val vehicleName = vehicle.buildFormattedName(context)
        val message = DKResource.buildString(
            context, DKColors.mainFontColor,
            DKColors.mainFontColor, R.string.dk_vehicle_delete_confirm, vehicleName
        )
        val alert = DKAlertDialog.LayoutBuilder().init(context)
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .cancelable(true)
            .positiveButton(positiveListener = { _, _ ->
                DriveKitVehicle.deleteVehicle(vehicle, object : VehicleDeleteQueryListener {
                    override fun onResponse(status: VehicleManagerStatus) {
                        if (status == VehicleManagerStatus.SUCCESS) {
                            viewModel.fetchVehicles(context)
                        } else {
                            displayError(context)
                        }
                    }
                })
            })
            .negativeButton()
            .show()

        val titleTextView = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = alert.findViewById<TextView>(R.id.text_view_alert_description)

        titleTextView?.setText(R.string.app_name)
        descriptionTextView?.text = message
    }

    private fun displayError(context: Context) {
        Toast.makeText(context, R.string.dk_vehicle_error_message, Toast.LENGTH_SHORT).show()
    }
}
