package com.drivequant.drivekit.ui.commons.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.ui.R
import kotlinx.android.synthetic.main.dk_distraction_item.view.*

internal class DistractionItem : LinearLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_distraction_item, null)
        view.text_view_distraction_event.headLine2()
        view.text_view_distraction_content.smallText(ContextCompat.getColor(
            context,
            R.color.dkDistractionContentCallColor
        ))
        view.line_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun setDistractionEventContent(title: String, content: String) {
        text_view_distraction_event.text = title
        text_view_distraction_content.text = content
    }

    fun setDistractionContentColor(selected: Boolean) {
        text_view_distraction_content.setTextColor(
            if (selected) DriveKitUI.colors.secondaryColor() else ContextCompat.getColor(
                context,
                R.color.dkDistractionContentCallColor
            )
        )
    }
}