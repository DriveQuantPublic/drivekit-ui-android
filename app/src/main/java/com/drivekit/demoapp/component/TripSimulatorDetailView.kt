package com.drivekit.demoapp.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivekit.demoapp.simulator.viewmodel.TripSimulatorDetailViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import kotlinx.android.synthetic.main.trip_simulator_detail_item.view.*

class TripSimulatorDetailView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private lateinit var viewModel: TripSimulatorDetailViewModel

    init {
        val view = View.inflate(context, R.layout.trip_simulator_detail_item, null)

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        setStyle()
    }

    private fun setStyle() {
        text_view_item_title.normalText(DriveKitUI.colors.complementaryFontColor())
        text_view_item_value.headLine1(DriveKitUI.colors.complementaryFontColor())
    }

    fun setItemTitle(title: String) {
        text_view_item_title.text = title
    }

    fun setItemValue(value: String) {
        text_view_item_value.text = value
    }
}