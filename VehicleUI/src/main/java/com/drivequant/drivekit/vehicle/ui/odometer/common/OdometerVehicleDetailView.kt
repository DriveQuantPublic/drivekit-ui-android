package com.drivequant.drivekit.vehicle.ui.odometer.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemType
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemViewModel
import kotlinx.android.synthetic.main.dk_layout_odometer_distance_item.view.*

class OdometerVehicleDetailView : LinearLayout {

    fun init(attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.dk_layout_odometer_distance_item, null)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        if (attrs != null) {
            val a =
                context.obtainStyledAttributes(attrs, R.styleable.OdometerVehicleDetailView, 0, 0)
            try {
                a.getString(R.styleable.OdometerVehicleDetailView_odometerTitle)?.let {
                    text_view_odometer_distance_title.text = DKResource.convertToString(context, it)
                }
                a.getDrawable(R.styleable.OdometerVehicleDetailView_odometerCornerIcon)?.let {
                    image_view_info.setImageDrawable(it)
                }
            } finally {
                a.recycle()
            }
            setStyle()
        }
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    private fun setStyle() {
        image_view_info.apply {
            setColorFilter(DriveKitUI.colors.secondaryColor())
        }
        dk_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }

    fun configureOdometerItem(viewModel: OdometerItemViewModel, odometerItemType: OdometerItemType, listener: OdometerDrawableListener) {
        text_view_odometer_distance_description.text = viewModel.getDescription(context, odometerItemType)
        text_view_odometer_distance_value.text = viewModel.getDistance(context, odometerItemType)

        image_view_info.apply {
            setColorFilter(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                listener.onDrawableClicked(it, odometerItemType)
            }
        }
    }
}

interface OdometerDrawableListener {
    fun onDrawableClicked(view: View, odometerItemType: OdometerItemType)
}