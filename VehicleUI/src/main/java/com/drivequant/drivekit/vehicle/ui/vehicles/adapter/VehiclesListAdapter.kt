package com.drivequant.drivekit.vehicle.ui.vehicles.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.viewholder.VehicleViewHolder
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel

class VehiclesListAdapter(
    var context: Context?,
    private val viewModel: VehiclesListViewModel,
    private val vehicles: List<Vehicle>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var view: View
        lateinit var holder: VehicleViewHolder

        if (convertView == null){
            val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.view_vehicle_item_list, null)
            holder = VehicleViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as VehicleViewHolder
            view = convertView
        }

        holder.bind(getItem(position))

        return view
    }

    override fun getItem(position: Int): Vehicle {
        return vehicles[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return vehicles.size
    }

    override fun isEmpty(): Boolean {
        return false
    }
}