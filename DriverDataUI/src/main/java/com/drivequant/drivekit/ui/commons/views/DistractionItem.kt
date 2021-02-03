package com.drivequant.drivekit.ui.commons.views

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.ui.R
import kotlinx.android.synthetic.main.dk_distraction_item.view.*

class DistractionItem : LinearLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_distraction_item, null)
        view.text_view_distraction_event.headLine2()
        view.text_view_distraction_content.smallText(DriveKitUI.colors.complementaryFontColor())
        view.line_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun setDistractionEvent(value: String) {
        text_view_distraction_event.text = value
    }

    fun setDistractionContent(value: String) {
        text_view_distraction_content.text = value
    }
}