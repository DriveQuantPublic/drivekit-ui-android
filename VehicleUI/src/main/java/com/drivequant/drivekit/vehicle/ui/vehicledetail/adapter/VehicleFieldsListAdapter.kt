package com.drivequant.drivekit.vehicle.ui.vehicledetail.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewholder.VehicleFieldViewHolder
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.GroupField
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel

class VehicleFieldsListAdapter(
    val context: Context,
    val viewModel: VehicleDetailViewModel)
    : RecyclerView.Adapter<VehicleFieldViewHolder>() {

    override fun onCreateViewHolder(viewgroup: ViewGroup, position: Int): VehicleFieldViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_vehicle_field_item_list, viewgroup, false)
        return VehicleFieldViewHolder(view, this, viewModel)
    }

    override fun onBindViewHolder(holder: VehicleFieldViewHolder, position: Int) {
        holder.bind(viewModel.groupFields[position])
    }

    override fun getItemCount() = viewModel.groupFields.size

    fun setGroupFields(groupFields: MutableList<GroupField>) {
        this.viewModel.groupFields = groupFields
    }
}