package com.drivequant.drivekit.vehicle.ui.vehicledetail.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewholder.VehicleFieldViewHolder
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.GroupField
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.VehicleDetailViewModel

class VehicleFieldsListAdapter(
    var context: Context,
    var viewModel: VehicleDetailViewModel,
    private var groupFields: List<GroupField>)
    : RecyclerView.Adapter<VehicleFieldViewHolder>() {

    override fun onCreateViewHolder(viewgroup: ViewGroup, position: Int): VehicleFieldViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_vehicle_field_item_list, viewgroup, false)
        return VehicleFieldViewHolder(view, this, viewModel)
    }

    override fun onBindViewHolder(holder: VehicleFieldViewHolder, position: Int) {
        holder.bind(groupFields[position])
    }

    override fun getItemCount(): Int {
        return groupFields.size
    }

    fun setGroupFields(groupFields: List<GroupField>) {
        this.groupFields = groupFields
    }
}