package com.drivequant.drivekit.vehicle.ui.vehicles.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.computeSubtitle
import com.drivequant.drivekit.vehicle.ui.extension.computeTitle

class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textViewTitle: TextView = itemView.findViewById(R.id.text_view_title)
    private val textViewSubtitle: TextView = itemView.findViewById(R.id.text_view_subtitle)
    private val textViewDetectionModeTitle: TextView = itemView.findViewById(R.id.text_view_detection_mode_title)

    fun bind(vehicle: Vehicle){
        val context = itemView.context
        textViewTitle.text = vehicle.computeTitle(context)
        textViewSubtitle.text = vehicle.computeSubtitle(context)
        textViewDetectionModeTitle.text = context.getString(R.string.dk_vehicle_detection_mode_title)
    }
}