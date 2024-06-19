package com.drivekit.demoapp.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.smallText

internal class TripSimulatorDetailView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val itemTitleTextView: TextView
    private val itemValueTextView: TextView

    init {
        val view = View.inflate(context, R.layout.trip_simulator_detail_item, null)

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        this.itemTitleTextView = view.findViewById(R.id.text_view_item_title)
        this.itemValueTextView = view.findViewById(R.id.text_view_item_value)

        setStyle()
    }

    private fun setStyle() {
        itemTitleTextView.smallText()
        itemValueTextView.headLine1()
    }

    fun setItemTitle(title: String) {
        itemTitleTextView.text = title
    }

    fun setItemValue(value: String) {
        itemValueTextView.text = value
    }
}
