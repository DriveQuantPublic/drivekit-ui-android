package com.drivequant.drivekit.timeline.ui.component.dateselector

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.timeline.ui.R
import kotlinx.android.synthetic.main.dk_date_selector_view.view.*

class DateSelectorView(context: Context) : LinearLayout(context) {

    private lateinit var viewModel: DateSelectorViewModel

    init {
        val view = View.inflate(context, R.layout.dk_date_selector_view, null).setDKStyle()
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun configure(viewModel: DateSelectorViewModel) {
        this.viewModel = viewModel
        this.viewModel.listener = object : DateSelectorListener {
            override fun onDateSelected(date: String) {
                update()
            }
        }
        update()
    }

    fun update() {
        layout_image_view_previous.setOnClickListener {
            viewModel.moveToPreviousDate()
        }

        layout_image_view_next.setOnClickListener {
            viewModel.moveToNextDate()
        }

        context?.let { context ->
            viewModel.hasPreviousDate.let {
                DKResource.convertToDrawable(context, "dk_timeline_previous")?.let { drawable ->
                    if (!it) {
                        drawable.tintDrawable(DriveKitUI.colors.complementaryFontColor())
                    } else {
                        drawable.tintDrawable(DriveKitUI.colors.secondaryColor())
                    }
                    layout_image_view_previous.isEnabled = it
                    image_view_previous.setImageDrawable(drawable)
                }
            }

            viewModel.hasNextDate.let {
                DKResource.convertToDrawable(context, "dk_timeline_next")?.let { drawable ->
                    if (!it) {
                        drawable.tintDrawable(DriveKitUI.colors.complementaryFontColor())
                    } else {
                        drawable.tintDrawable(DriveKitUI.colors.secondaryColor())
                    }
                    layout_image_view_next.isEnabled = it
                    image_view_next.setImageDrawable(drawable)
                }
            }

            val fromDatePrefix = DKResource.convertToString(context, "dk_timeline_from_date")
            val toDatePrefix = DKResource.convertToString(context, "dk_timeline_to_date")

            text_view_date_range.text = DKSpannable()
                .append(fromDatePrefix, context.resSpans {
                    typeface(Typeface.BOLD)
                    color(DriveKitUI.colors.complementaryFontColor())
                    }
                )
                .space()
                .append(
                    viewModel.computedFromDate.formatDate(DKDatePattern.STANDARD_DATE),
                    context.resSpans {
                        typeface(Typeface.BOLD)
                        color(DriveKitUI.colors.primaryColor())
                    }
                )
                .space()
                .append(toDatePrefix, context.resSpans {
                    typeface(Typeface.BOLD)
                    color(DriveKitUI.colors.complementaryFontColor())
                })
                .space()
                .append(
                    viewModel.computedToDate.formatDate(DKDatePattern.STANDARD_DATE),
                context.resSpans {
                    typeface(Typeface.BOLD)
                    color(DriveKitUI.colors.primaryColor())
                })
                .toSpannable()
        }
    }
}

