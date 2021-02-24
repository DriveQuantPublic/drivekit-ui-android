package com.drivequant.drivekit.vehicle.ui.picker.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleItemListFragment
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleItemListFragment.AdapterType.*
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem

class ItemRecyclerViewAdapter(
    private val currentPickerStep: VehiclePickerStep,
    private val data: List<VehiclePickerItem>,
    private val listener: VehicleItemListFragment.OnListFragmentInteractionListener?
): RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    private val adapterType = VehicleItemListFragment.AdapterType.getAdapterTypeByPickerStep(currentPickerStep)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = when (adapterType){
            TEXT_ITEM_PADDING -> R.layout.layout_item_text_padding
            TEXT_ITEM -> R.layout.layout_item_text
            TEXT_IMAGE_ITEM,
            TEXT_OR_IMAGE_ITEM,
            TRUCK_TYPE_ITEM -> R.layout.layout_item_text_image
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        FontUtils.overrideFonts(parent.context, view)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: VehiclePickerItem = data[position]
        holder.item = item
        holder.textView.text = item.text
        holder.imageView?.let {
            if (item.icon1 != null) {
                it.setImageDrawable(item.icon1)
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }
        holder.view.setOnClickListener {
            holder.item?.let {
                listener?.onSelectedItem(currentPickerStep, it)
            }
        }

        holder.textView.visibility = View.VISIBLE
        when (adapterType){
            TEXT_ITEM,
            TEXT_ITEM_PADDING -> {
                holder.textView.bigText(DriveKitUI.colors.fontColorOnSecondaryColor())
                holder.textView.background?.tintDrawable(DriveKitUI.colors.secondaryColor())
            }
            TEXT_IMAGE_ITEM -> {
                holder.textView.normalText(DriveKitUI.colors.complementaryFontColor())
            }
            TEXT_OR_IMAGE_ITEM -> {
                if (item.value.equals("OTHER_BRANDS", true)){
                    holder.textView.normalText()
                    holder.textView.bigText(DriveKitUI.colors.complementaryFontColor())
                } else {
                    holder.textView.visibility = View.GONE
                }
            }
            TRUCK_TYPE_ITEM -> {
                // do nothing
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val view = mView
        val textView: TextView = mView.findViewById<View>(R.id.text_view) as TextView
        val imageView: ImageView? = mView.findViewById<View>(R.id.image_view) as ImageView?
        var item: VehiclePickerItem? = null
    }
}