package com.drivequant.drivekit.vehicle.ui.vehicles.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.viewholder.DetectionModeSpinnerItem

/**
 * Created by Mohamed on 2020-03-13.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

open class DetectionModeSpinnerAdapter(
    context: Context,
    list: List<DetectionModeSpinnerItem>
) : ArrayAdapter<DetectionModeSpinnerItem>(context, R.layout.simple_list_item_spinner, list) {

    private var detectionModes: List<DetectionModeSpinnerItem> = list

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        getCustomView(position, parent)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        getCustomView(position, parent)

    private fun getCustomView(position: Int, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_list_item_spinner, parent, false)
        val detectionMode = detectionModes[position]
        val textViewDetectionMode = view.findViewById<TextView>(R.id.text_view)
        textViewDetectionMode.text = detectionMode.detectionModeType.getTitle(context)
        FontUtils.overrideFonts(context, view)
        return view
    }
}