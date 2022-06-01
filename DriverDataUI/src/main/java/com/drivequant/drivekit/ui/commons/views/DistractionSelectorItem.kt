package com.drivequant.drivekit.ui.commons.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.ui.R
import kotlinx.android.synthetic.main.dk_distraction_selector_item.view.*

internal class DistractionSelectorItem : LinearLayout {

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
    }

    fun setSelectorContent(content: String) {
        distraction_selector.text = content
    }

    fun setSelection(selected: Boolean) {
        val drawable = (distraction_selector.background as GradientDrawable).mutate()
        DrawableCompat.setTint(
            drawable,
            if (selected) DriveKitUI.colors.secondaryColor()
            else ContextCompat.getColor(context, R.color.dkDistractionSelectorColor)
        )
        distraction_selector.smallText(
            textColor = if (selected) DriveKitUI.colors.fontColorOnSecondaryColor() else DriveKitUI.colors.primaryColor(),
            isTypeFaceBold = true
        )
    }
}