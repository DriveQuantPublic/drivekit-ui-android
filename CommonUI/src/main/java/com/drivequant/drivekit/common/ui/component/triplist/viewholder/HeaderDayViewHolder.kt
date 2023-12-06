package com.drivequant.drivekit.common.ui.component.triplist.viewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.R

internal class HeaderDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvInformations: TextView = view.findViewById(R.id.text_view_informations)
    val tvDate: TextView = view.findViewById(R.id.text_view_date)
    val background: LinearLayout = view.findViewById(R.id.trip_list_header)
}
