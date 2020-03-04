package com.drivequant.drivekit.vehicle.ui.vehicles.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.drivequant.drivekit.databaseutils.entity.DetectionMode.*
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel

class VehicleViewHolder(itemView: View, var viewModel: VehiclesListViewModel) : RecyclerView.ViewHolder(itemView) {
    private val textViewTitle: TextView = itemView.findViewById(R.id.text_view_title)
    private val textViewSubtitle: TextView = itemView.findViewById(R.id.text_view_subtitle)
    private val textViewDetectionModeTitle: TextView = itemView.findViewById(R.id.text_view_detection_mode_title)
    private val textViewDetectionModeDescription: TextView = itemView.findViewById(R.id.text_view_detection_mode_description)
    private val buttonSetup: Button = itemView.findViewById(R.id.text_view_setup_button)

    fun bind(vehicle: Vehicle){
        val context = itemView.context
        textViewTitle.text = viewModel.getTitle(context, vehicle)
        textViewSubtitle.text = viewModel.getSubtitle(context, vehicle)
        textViewDetectionModeTitle.text = context.getString(R.string.dk_vehicle_detection_mode_title)
        textViewDetectionModeDescription.text = viewModel.getDetectionModeDescription(context, vehicle)

        setupConfigureButton(context, vehicle)
    }

    private fun setupConfigureButton(context: Context, vehicle: Vehicle){
         when (vehicle.detectionMode){
             DISABLED, GPS -> {
                 buttonSetup.visibility = View.GONE
             }
             BEACON -> {
                 buttonSetup.visibility = View.VISIBLE
                 buttonSetup.text = context.getString(R.string.dk_vehicle_configure_beacon)
             }
             BLUETOOTH -> {
                 buttonSetup.visibility = View.VISIBLE
                 buttonSetup.text = context.getString(R.string.dk_vehicle_configure_bluetooth)
             }
         }
    }
}