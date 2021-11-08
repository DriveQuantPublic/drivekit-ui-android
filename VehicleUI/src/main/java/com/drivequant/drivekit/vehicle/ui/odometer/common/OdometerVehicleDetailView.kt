package com.drivequant.drivekit.vehicle.ui.odometer.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.highlightBig
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemType
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemViewModel
import kotlinx.android.synthetic.main.dk_layout_odometer_distance_item.view.*

internal class OdometerVehicleDetailView : LinearLayout {

    fun init(attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.dk_layout_odometer_distance_item, null)
        addView(view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        if (attrs != null) {
            val a =
                context.obtainStyledAttributes(attrs, R.styleable.OdometerVehicleDetailView, 0, 0)
            try {
                a.getString(R.styleable.OdometerVehicleDetailView_odometerTitle)?.let {
                    text_view_odometer_distance_title.apply {
                        text = DKResource.convertToString(context, it)
                        setTextColor(Color.BLACK)
                    }
                }
                a.getDrawable(R.styleable.OdometerVehicleDetailView_odometerCornerIcon)?.let {
                    image_view_info.setImageDrawable(it)
                }
            } finally {
                a.recycle()
            }
            dk_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        }
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    fun configureOdometerItem(viewModel: OdometerItemViewModel, odometerItemType: OdometerItemType, listener: OdometerDrawableListener) {
        text_view_odometer_distance_description.apply {
            text = viewModel.getDescription(context, odometerItemType)
            smallText(ContextCompat.getColor(context, R.color.dkGrayColor))
        }
        text_view_odometer_distance_value.apply {
            text = viewModel.getDistance(context, odometerItemType)
            setTextColor(ContextCompat.getColor(context, R.color.dkGrayColor))
        }

        image_view_info.apply {
            setColorFilter(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                listener.onDrawableClicked(it, odometerItemType)
            }
        }
    }
}

internal interface OdometerDrawableListener {
    fun onDrawableClicked(view: View, odometerItemType: OdometerItemType)
}