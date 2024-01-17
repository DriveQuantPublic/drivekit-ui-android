package com.drivequant.drivekit.vehicle.ui.odometer.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.highlightBig
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemType
import com.drivequant.drivekit.vehicle.ui.odometer.viewmodel.OdometerItemViewModel

internal class OdometerVehicleDetailView : LinearLayout {

    private lateinit var odometerDistanceTitle: TextView
    private lateinit var odometerDistanceDescription: TextView
    private lateinit var odometerDistanceValue: TextView
    private lateinit var infoImageView: ImageView
    private lateinit var separatorView: View

    fun init(attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.dk_layout_odometer_distance_item, null)
        addView(view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        this.odometerDistanceTitle = view.findViewById(R.id.text_view_odometer_distance_title)
        this.odometerDistanceDescription = view.findViewById(R.id.text_view_odometer_distance_description)
        this.odometerDistanceValue = view.findViewById(R.id.text_view_odometer_distance_value)
        this.infoImageView = view.findViewById(R.id.image_view_info)
        this.separatorView = view.findViewById(R.id.dk_view_separator)

        if (attrs != null) {
            val a =
                context.obtainStyledAttributes(attrs, R.styleable.OdometerVehicleDetailView, 0, 0)
            try {
                a.getString(R.styleable.OdometerVehicleDetailView_odometerTitle)?.let {
                    odometerDistanceTitle.apply {
                        text = DKResource.convertToString(context, it)
                        setTextColor(Color.BLACK)
                    }
                }
                a.getDrawable(R.styleable.OdometerVehicleDetailView_odometerCornerIcon)?.let {
                    infoImageView.setImageDrawable(it)
                }
            } finally {
                a.recycle()
            }
            separatorView.setBackgroundColor(DriveKitUI.colors.neutralColor())
        }
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    fun configureOdometerItem(viewModel: OdometerItemViewModel, odometerItemType: OdometerItemType, listener: OdometerDrawableListener) {
        odometerDistanceDescription.apply {
            text = viewModel.getDescription(context, odometerItemType)
            smallText(ContextCompat.getColor(context, com.drivequant.drivekit.common.ui.R.color.dkGrayColor))
        }
        odometerDistanceValue.apply {
            text = viewModel.getDistance(context, odometerItemType)
            highlightBig()
        }

        infoImageView.apply {
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
