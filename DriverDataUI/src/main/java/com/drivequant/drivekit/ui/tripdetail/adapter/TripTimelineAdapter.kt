package com.drivequant.drivekit.ui.tripdetail.adapter

import android.graphics.Color
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewholder.OnItemClickListener
import com.drivequant.drivekit.ui.tripdetail.viewholder.TripTimelineItemViewHolder
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripEvent
import kotlinx.android.synthetic.main.trip_timeline_item.view.*

class TripTimelineAdapter(
    private val items: List<TripEvent>,
    private val listener : OnItemClickListener,
    private val selectedBackgroundColor: Int) : RecyclerView.Adapter<TripTimelineItemViewHolder>() {

    var selectedPosition = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): TripTimelineItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_timeline_item, parent, false)
        FontUtils.overrideFonts(parent.context, view)
        return TripTimelineItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: TripTimelineItemViewHolder, position: Int) {
        viewHolder.bind(items[position], position == 0, position == itemCount - 1, listener)
        if (selectedPosition == position){
            viewHolder.itemView.setBackgroundColor(ColorUtils.setAlphaComponent(selectedBackgroundColor, 127))
            viewHolder.itemView.event_hour.setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
            viewHolder.itemView.event_description.setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
            viewHolder.itemView.event_image.drawable.tintDrawable(DriveKitUI.colors.fontColorOnSecondaryColor())
        }else{
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
            viewHolder.itemView.event_image.drawable.tintDrawable(DriveKitUI.colors.mainFontColor())
        }
    }

}