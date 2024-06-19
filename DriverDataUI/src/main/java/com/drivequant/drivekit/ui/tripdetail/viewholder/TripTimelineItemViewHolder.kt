package com.drivequant.drivekit.ui.tripdetail.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripEvent

class TripTimelineItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val eventHour: TextView = itemView.findViewById(R.id.event_hour)
    private val eventDescription: TextView = itemView.findViewById(R.id.event_description)
    private val eventImage: ImageView = itemView.findViewById(R.id.event_image)
    private val lineTop: View = itemView.findViewById(R.id.line_top)
    private val lineBottom: View = itemView.findViewById(R.id.line_bottom)

    fun bind(tripEvent : TripEvent, isFirst: Boolean, isLast: Boolean, listener :OnItemClickListener){
        eventHour.text = tripEvent.time.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
        eventDescription.text = tripEvent.getTitle(itemView.context)
        eventImage.setImageResource(tripEvent.getEventImageResource())
        if (isFirst) lineTop.visibility = View.INVISIBLE else lineTop.visibility = View.VISIBLE
        if (isLast) lineBottom.visibility = View.INVISIBLE else lineBottom.visibility = View.VISIBLE
        lineTop.setBackgroundColor(DriverDataUI.mapTraceMainColor)
        lineBottom.setBackgroundColor(DriverDataUI.mapTraceMainColor)

        itemView.setOnClickListener {
            listener.onItemClicked(adapterPosition)
        }
    }
}

interface OnItemClickListener {
    fun onItemClicked(position: Int)
}
