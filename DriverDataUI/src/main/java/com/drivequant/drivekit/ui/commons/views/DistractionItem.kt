package com.drivequant.drivekit.ui.commons.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.ui.R

internal class DistractionItem : LinearLayout {

    private lateinit var distractionEventTextView: TextView
    private lateinit var distractionContentTextView: TextView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_distraction_item, null)
        this.distractionEventTextView = view.findViewById(R.id.text_view_distraction_event)
        this.distractionContentTextView = view.findViewById(R.id.text_view_distraction_content)
        distractionEventTextView.headLine2()
        distractionContentTextView.smallText()
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun setDistractionEventContent(title: String, content: String) {
        distractionEventTextView.text = title
        distractionContentTextView.text = content
    }

    fun setDistractionContentColor(selected: Boolean) {
        if (selected) {
            DKColors.secondaryColor
        } else {
            DKColors.complementaryFontColor
        }.let {
            distractionContentTextView.setTextColor(it)
        }
    }
}
