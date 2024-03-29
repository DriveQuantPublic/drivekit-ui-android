package com.drivequant.drivekit.tripanalysis.workinghours.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.tripanalysis.workinghours.viewholder.HoursSpinnerItem

internal class WorkingHoursSpinnerAdapter(
    context: Context,
    list: List<HoursSpinnerItem>
) : ArrayAdapter<HoursSpinnerItem>(context, com.drivequant.drivekit.common.ui.R.layout.dk_simple_list_item_spinner, list) {

    private var tripSlotStatusList: List<HoursSpinnerItem> = list

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        getCustomView(position, parent)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        getCustomView(position, parent)

    private fun getCustomView(position: Int, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.drivequant.drivekit.common.ui.R.layout.dk_simple_list_item_spinner, parent, false)
        val timeSlotStatus = tripSlotStatusList[position]
        val textView = view.findViewById<TextView>(R.id.text_view)
        textView.text = timeSlotStatus.toString()
        FontUtils.overrideFonts(context, view)
        return view
    }
}
