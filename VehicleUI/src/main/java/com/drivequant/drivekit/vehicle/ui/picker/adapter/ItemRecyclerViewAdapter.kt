package com.drivequant.drivekit.vehicle.ui.picker.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleItemListFragment
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem

class ItemRecyclerViewAdapter(private val vehiclePickerStep: VehiclePickerStep, private val items: List<VehiclePickerItem>, private val listener: VehicleItemListFragment.OnListFragmentInteractionListener?):
    RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterType = VehicleItemListFragment.AdapterType.getAdapterTypeByPickerStep(vehiclePickerStep)
        val view = if (adapterType == VehicleItemListFragment.AdapterType.TEXT_IMAGE_ITEM){
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_text_image, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_text, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: VehiclePickerItem = items[position]
        holder.item = items[position]
        holder.textView.text = item.text
        holder.imageView?.let {
            if (item.icon1 != null) {
                it.setImageDrawable(item.icon1)
            }
        }
        holder.view.setOnClickListener {
            holder.item?.let {
                listener?.onSelectedItem(vehiclePickerStep, it)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val view = mView
        val textView: TextView = mView.findViewById<View>(R.id.text_view) as TextView
        val imageView: ImageView? = mView.findViewById<View>(R.id.image_view) as ImageView?
        var item: VehiclePickerItem? = null
    }
}