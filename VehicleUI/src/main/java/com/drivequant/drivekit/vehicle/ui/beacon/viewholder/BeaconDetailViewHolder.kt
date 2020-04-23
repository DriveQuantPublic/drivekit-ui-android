package com.drivequant.drivekit.vehicle.ui.beacon.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconDetailField

class BeaconDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val title = itemView.findViewById<TextView>(R.id.text_view_title)
    private val value = itemView.findViewById<TextView>(R.id.text_view_value)
    private val separator = itemView.findViewById<View>(R.id.view_separator)

    fun bind(lineData: BeaconDetailField){
        title.normalText(DriveKitUI.colors.mainFontColor())
        title.text = lineData.title
        value.text = lineData.value
        separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}