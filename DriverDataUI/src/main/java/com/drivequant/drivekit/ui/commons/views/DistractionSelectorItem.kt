package com.drivequant.drivekit.ui.commons.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v4.graphics.ColorUtils
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.ui.R
import kotlinx.android.synthetic.main.dk_distraction_selector_item.view.*

class DistractionSelectorItem : LinearLayout {

    private lateinit var selectorBackground: GradientDrawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_distraction_selector_item, null)
        view.distraction_selector.smallText()
        selectorBackground = view.distraction_selector.background as GradientDrawable
        selectorBackground.setStroke(3, DriveKitUI.colors.complementaryFontColor())
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun setSelectorContent(content: String) {
        distraction_selector.text = content
    }

    fun setSelection(selected: Boolean) {
        selectorBackground.setStroke(3, DriveKitUI.colors.complementaryFontColor())
        val drawable = selectorBackground.mutate()
        DrawableCompat.setTint(
            drawable,
            if (selected) ColorUtils.setAlphaComponent(DriveKitUI.colors.secondaryColor(), 102) else Color.parseColor("#F3F3F3")
        )
    }
}