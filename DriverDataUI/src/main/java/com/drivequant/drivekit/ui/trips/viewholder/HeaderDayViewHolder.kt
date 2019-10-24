package com.drivequant.drivekit.ui.trips.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.group_item_trip_list.view.*

class HeaderDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvInformations: TextView = view.text_view_informations
    val tvDate: TextView = view.text_view_date
}