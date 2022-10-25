package com.drivequant.drivekit.timeline.ui.component.periodselector

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.timeline.getTitleResId
import kotlinx.android.synthetic.main.dk_timeline_period_selector.view.*

@SuppressLint("ViewConstructor")
class PeriodSelectorView(
    context: Context,
    val timelinePeriod: DKTimelinePeriod,
    val listener: PeriodSelectorListener
) : LinearLayout(context) {

    init {
        val view = View.inflate(context, R.layout.dk_timeline_period_selector, null).setDKStyle()
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        setStyle()
        with(text_view_selector) {
            text = DKResource.convertToString(context, timelinePeriod.getTitleResId())
            setOnClickListener {
                listener.onSelectPeriod(timelinePeriod)
            }
        }
    }

    private fun setStyle() {
        val params = text_view_selector.layoutParams as MarginLayoutParams
        params.setMargins(
            context.resources.getDimension(R.dimen.dk_margin_quarter).toInt(),
            params.topMargin,
            context.resources.getDimension(R.dimen.dk_margin_quarter).toInt(),
            params.bottomMargin
        )
        layoutParams = params
        text_view_selector.normalText()
        (text_view_selector.background as GradientDrawable).setColor(DriveKitUI.colors.neutralColor())
    }

    fun setPeriodSelected(selected: Boolean) {
        if (selected) {
            (text_view_selector.background as GradientDrawable).setColor(DriveKitUI.colors.secondaryColor())
            text_view_selector.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        } else {
            (text_view_selector.background as GradientDrawable).setColor(DriveKitUI.colors.neutralColor())
            text_view_selector.normalText()
        }
    }
}

