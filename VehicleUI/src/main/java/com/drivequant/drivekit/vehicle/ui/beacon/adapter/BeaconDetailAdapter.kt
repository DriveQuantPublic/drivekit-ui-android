package com.drivequant.drivekit.vehicle.ui.beacon.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewholder.BeaconDetailViewHolder
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconDetailViewModel

class BeaconDetailAdapter(
    var context: Context,
    var viewModel: BeaconDetailViewModel
) : RecyclerView.Adapter<BeaconDetailViewHolder>() {

    override fun onCreateViewHolder(viewgroup: ViewGroup, position: Int): BeaconDetailViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.beacon_detail_list_item, viewgroup, false).setDKStyle()
        return BeaconDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return viewModel.data.size
    }

    override fun onBindViewHolder(holder: BeaconDetailViewHolder, position: Int) {
        val lineData = viewModel.data[position]
        holder.bind(lineData)
    }
}