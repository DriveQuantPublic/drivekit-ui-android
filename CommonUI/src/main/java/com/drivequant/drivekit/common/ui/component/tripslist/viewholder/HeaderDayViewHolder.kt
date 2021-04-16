package com.drivequant.drivekit.common.ui.component.tripslist.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.group_item_trip_list.view.*

internal class HeaderDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvInformations: TextView = view.text_view_informations
    val tvDate: TextView = view.text_view_date
    val background: LinearLayout = view.trip_list_header
}