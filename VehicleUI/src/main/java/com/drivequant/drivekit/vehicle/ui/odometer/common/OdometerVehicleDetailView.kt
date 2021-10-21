package com.drivequant.drivekit.vehicle.ui.odometer.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import kotlinx.android.synthetic.main.dk_layout_odometer_distance_item.view.*

class OdometerVehicleDetailView : LinearLayout {

    fun init() {
        val view = View.inflate(context, R.layout.dk_layout_odometer_distance_item, null)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setStyle()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun setStyle() {
        image_view_info.apply {
            setImageDrawable(DKResource.convertToDrawable(context, "dk_common_info"))
            setColorFilter(DriveKitUI.colors.secondaryColor())
        }
        dk_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }

    fun setTitle(title: String) {
        text_view_odometer_distance_title.text = title
    }

    fun setDescription(description: String) {
        text_view_odometer_distance_description.text = description
    }

    fun setOdometerDistance(distance: String) {
        text_view_odometer_distance_value.text = distance
    }
}