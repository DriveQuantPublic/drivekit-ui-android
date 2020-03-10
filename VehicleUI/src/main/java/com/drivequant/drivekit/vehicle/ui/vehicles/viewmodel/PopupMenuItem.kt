package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.content.Context
import android.content.DialogInterface
import android.widget.TextView
import android.widget.Toast
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicleManager
import com.drivequant.drivekit.vehicle.manager.VehicleDeleteQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleManagerStatus
import com.drivequant.drivekit.vehicle.ui.R

enum class PopupMenuItem(
    private val descriptionIdentifier: String
) : MenuItem {
    SHOW("dk_vehicle_show"),
    REPLACE("dk_vehicle_replace"),
    DELETE("dk_vehicle_delete");

    override fun getTitle(context: Context): String {
        val stringValue = DKResource.convertToString(context, descriptionIdentifier)
        return stringValue?.let { it }?: run { "" }
    }

    override fun isDisplayable(vehicle: Vehicle): Boolean {
        // TODO handle other cases with singleton
        when (this){
            SHOW -> return !vehicle.liteConfig
            else -> return true
        }
    }

    override fun onItemClicked(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle) {
        when (this) {
            SHOW -> { } // TODO launch vehicle detail screen
            REPLACE -> { } // TODO launch vehicle picker in replace mode
            DELETE -> {
                manageDeleteVehicle(context, viewModel, vehicle)
            }
        }
    }

    private fun manageDeleteVehicle(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle){
        val title = DKResource.convertToString(context, "app_name")?.let { it }?: run { "" }
        val message = DKResource.convertToString(context, "dk_vehicle_delete_confirm")?.let { it }?: run { ""}

        val alert = DKAlertDialog.LayoutBuilder().init(context)
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(true)
            .positiveButton(context.getString(R.string.dk_common_ok),
                DialogInterface.OnClickListener { dialog, _ ->
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