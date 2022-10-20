package com.drivequant.drivekit.timeline.ui.component.roadcontext.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.timeline.ui.R

import com.drivequant.drivekit.timeline.ui.component.roadcontext.RoadContextType
import kotlinx.android.synthetic.main.dk_road_context_list_item.view.*


internal class RoadContextItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(roadContext: RoadContextType) {
        val drawable = ContextCompat.getDrawable(
            itemView.context,
            R.drawable.dk_road_context_indicator
        )?.mutate()
        drawable?.tintDrawable(ContextCompat.getColor(itemView.context, roadContext.getColorResId()))
        itemView.image_view_road_context_indicator.setImageDrawable(drawable)
        itemView.text_view_road_context_name.text =
            DKResource.convertToString(itemView.context, roadContext.getTitleResId())
    }

    fun setVisibility(isVisible: Boolean) {
        if (isVisible) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }.let {
            itemView.visibility = it
        }
    }
}
