package com.drivequant.drivekit.common.ui.component.dateselector

import android.content.Context
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tint
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.getValue
import com.drivequant.drivekit.databaseutils.entity.DKPeriod

class DKDateSelectorView(context: Context) : LinearLayout(context) {

    private lateinit var viewModel: DKDateSelectorViewModel
    private val previousButton: ImageView
    private val nextButton: ImageView
    private val dateTextView: TextView

    init {
        val view = View.inflate(context, R.layout.dk_date_selector_view, null).setDKStyle()
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        this.previousButton = view.findViewById(R.id.image_view_previous)
        this.nextButton = view.findViewById(R.id.image_view_next)
        this.dateTextView = view.findViewById(R.id.text_view_date_range)
    }

    fun configure(viewModel: DKDateSelectorViewModel) {
        this.viewModel = viewModel
        update()
    }

    private fun update() {
        previousButton.setOnClickListener {
            viewModel.moveToPreviousDate()
        }

        nextButton.setOnClickListener {
            viewModel.moveToNextDate()
        }

        context?.let { context ->
            viewModel.hasPreviousDate.let { hasPreviousDate ->
                ContextCompat.getDrawable(context, R.drawable.dk_common_arrow_back_thick)?.let { drawable ->
                    val color = if (!hasPreviousDate) {
                        R.color.neutralColor
                    } else {
                        R.color.secondaryColor
                    }
                    drawable.tint(context, color)
                    previousButton.isEnabled = hasPreviousDate
                    previousButton.setImageDrawable(drawable)
                }
            }

            viewModel.hasNextDate.let { hasNextDate ->
                ContextCompat.getDrawable(context, R.drawable.dk_common_arrow_forward_thick)?.let { drawable ->
                    val color = if (!hasNextDate) {
                        R.color.neutralColor
                    } else {
                        R.color.secondaryColor
                    }
                    drawable.tint(context, color)
                    nextButton.isEnabled = hasNextDate
                    nextButton.setImageDrawable(drawable)
                }
            }

            dateTextView.text = when (viewModel.period) {
                null -> ""
                DKPeriod.WEEK -> getWeekDateText()
                DKPeriod.MONTH -> getMonthDateText()
                DKPeriod.YEAR -> getYearDateText()
            }
        }
    }

    private fun getWeekDateText(): SpannableString {
        val fromDate = viewModel.fromDate
        val toDate = viewModel.toDate
        val text: String = if (fromDate != null && toDate != null) {
            val startMonth = fromDate.getValue(CalendarField.MONTH)
            val endMonth = toDate.getValue(CalendarField.MONTH)
            if (startMonth == endMonth) {
                "${fromDate.formatDate(DKDatePattern.DAY_OF_MONTH)} - ${toDate.formatDate(DKDatePattern.DAY_MONTH_LETTER_YEAR)}"
            } else {
                "${fromDate.formatDate(DKDatePattern.DAY_MONTH_LETTER_SHORT)} - ${toDate.formatDate(DKDatePattern.DAY_MONTH_LETTER_SHORT_YEAR)}"
            }
        } else {
            ""
        }
        return DKSpannable().append(
            context,
            text,
            DKColors.primaryColor,
            DKStyle.NORMAL_TEXT
        ).toSpannable()
    }

    private fun getMonthDateText(): SpannableString = DKSpannable().append(
        context,
        viewModel.fromDate?.formatDate(DKDatePattern.MONTH_LETTER_YEAR)?.capitalizeFirstLetter() ?: "",
        DKColors.primaryColor,
        DKStyle.NORMAL_TEXT
    ).toSpannable()

    private fun getYearDateText(): SpannableString = DKSpannable().append(
        context,
        viewModel.fromDate?.formatDate(DKDatePattern.YEAR) ?: "",
        DKColors.primaryColor,
        DKStyle.NORMAL_TEXT
    ).toSpannable()

}
