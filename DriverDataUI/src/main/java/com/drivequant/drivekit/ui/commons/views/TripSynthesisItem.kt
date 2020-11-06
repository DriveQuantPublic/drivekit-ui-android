package com.drivequant.drivekit.ui.commons.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.ui.R

class TripSynthesisItem: LinearLayout {

    private lateinit var textViewTitle: TextView
    private lateinit var textViewValue:TextView
    private lateinit var separator:TextView

    constructor(context: Context): super(context){
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.trip_synthesis_item, null)
        textViewTitle = view.findViewById(R.id.text_view_trip_synthesis_title)
        textViewValue = view.findViewById(R.id.text_view_trip_synthesis_value)
        separator = view.findViewById(R.id.line_separator)

        textViewValue.smallText(DriveKitUI.colors.primaryColor())
        textViewValue.setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
        textViewTitle.smallText(DriveKitUI.colors.complementaryFontColor())
        separator.setBackgroundColor(DriveKitUI.colors.neutralColor())

        if (attrs != null) {
            val a: TypedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TripSynthesisItem,
                0, 0
            )
            try {
                val title = a.getString(R.styleable.TripSynthesisItem_tripSynthesisTitle)
                title?.let { setTitleItem(it) }
            } finally {
                a.recycle()
            }
        }
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun setTitleItem(title: String) {
        textViewTitle.text = title
    }

    fun setValueItem(value: String?) {
        textViewValue.text = value
    }

    fun setValueColor() {
        textViewValue.setTextColor(DriveKitUI.colors.secondaryColor())
    }

    fun setValueTypeFace() {
        textViewValue.typeface = Typeface.DEFAULT_BOLD
    }

    fun onTripItemSynthesisClick(context: Context, vehicleId: String?, liteConfig: Boolean?) {
        liteConfig?.let {
            when {
                liteConfig -> {
                    DriveKitNavigationController.vehicleUIEntryPoint?.startVehicleListActivity(context)
                }
                vehicleId != null -> {
                    DriveKitNavigationController.vehicleUIEntryPoint?.startVehicleDetailActivity(context, vehicleId)
                }
                else -> {
                    // do nothing
                }
            }
        }
    }
}