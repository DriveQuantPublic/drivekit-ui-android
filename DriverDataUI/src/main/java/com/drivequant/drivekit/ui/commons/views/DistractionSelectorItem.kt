package com.drivequant.drivekit.ui.commons.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.smallTextWithColor
import com.drivequant.drivekit.common.ui.extension.tint
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.ui.R

internal class DistractionSelectorItem : LinearLayout {

    private lateinit var distractionSelectorTextView: TextView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_distraction_selector_item, null)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        this.distractionSelectorTextView = view.findViewById(R.id.distraction_selector)
    }

    fun setSelectorContent(content: String) {
        distractionSelectorTextView.text = content
    }

    fun setSelection(selected: Boolean) {
        val drawable = (distractionSelectorTextView.background as GradientDrawable)
        val color = if (selected) com.drivequant.drivekit.common.ui.R.color.secondaryColor else R.color.dkDistractionSelectorColor
        drawable.tint(context, color)
        distractionSelectorTextView.smallTextWithColor(
            textColor = if (selected) DKColors.fontColorOnSecondaryColor else DKColors.primaryColor,
            isTypeFaceBold = true
        )
    }
}
