package com.drivequant.drivekit.vehicle.ui.beacon.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconDetailField

class BeaconDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title = itemView.findViewById<TextView>(R.id.text_view_title)
    private val value = itemView.findViewById<TextView>(R.id.text_view_value)

    fun bind(lineData: BeaconDetailField) {
        title.normalText()
        title.text = lineData.title
        value.text = lineData.value
    }
}
