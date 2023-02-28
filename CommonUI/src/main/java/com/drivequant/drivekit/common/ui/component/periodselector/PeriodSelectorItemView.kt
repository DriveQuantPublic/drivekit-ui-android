package com.drivequant.drivekit.common.ui.component.periodselector

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.DKPeriod

@SuppressLint("ViewConstructor")
internal class PeriodSelectorItemView(
    context: Context,
    val period: DKPeriod,
    val listener: DKPeriodSelectorItemListener
) : LinearLayout(context) {

    private val textViewSelector: TextView

    init {
        val view = View.inflate(context, R.layout.dk_period_selector_item, null).setDKStyle()
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        this.textViewSelector = view.findViewById(R.id.text_view_selector)
        setStyle()
        with(this.textViewSelector) {
            text = DKResource.convertToString(context, period.getTitleResId())
            setOnClickListener {
                listener.onPeriodSelected(period)
            }
        }
    }

    private fun setStyle() {
        val params = textViewSelector.layoutParams as MarginLayoutParams
        params.setMargins(
            context.resources.getDimension(R.dimen.dk_margin_quarter).toInt(),
            params.topMargin,
            context.resources.getDimension(R.dimen.dk_margin_quarter).toInt(),
            params.bottomMargin
        )
        layoutParams = params
        textViewSelector.normalText()
        (textViewSelector.background as GradientDrawable).setColor(DriveKitUI.colors.neutralColor())
    }

    fun setPeriodSelected(selected: Boolean) {
        if (selected) {
            (textViewSelector.background as GradientDrawable).setColor(DriveKitUI.colors.secondaryColor())
            textViewSelector.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
        } else {
            (textViewSelector.background as GradientDrawable).setColor(DriveKitUI.colors.neutralColor())
            textViewSelector.normalText()
        }
    }

}
