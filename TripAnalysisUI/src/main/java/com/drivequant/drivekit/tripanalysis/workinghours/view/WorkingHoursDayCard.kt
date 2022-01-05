package com.drivequant.drivekit.tripanalysis.workinghours.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.CompoundButtonCompat
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHoursDayConfiguration
import com.drivequant.drivekit.tripanalysis.workinghours.toDay
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class WorkingHoursDayCard : FrameLayout {

    private lateinit var day: DKWorkingHoursDayConfiguration

    private lateinit var cardView: CardView
    private lateinit var sliderContainer: RelativeLayout
    private lateinit var rangeSlider: RangeSlider
    private lateinit var checkbox: CheckBox
    private lateinit var labelDay: TextView
    private lateinit var labelMin: TextView
    private lateinit var labelMax: TextView

    private var listener: WorkingHoursDayListener? = null

    constructor(context: Context, day: DKWorkingHoursDayConfiguration) : super(context) {
        init(day)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(day)
    }

    private fun init(dayConfig: DKWorkingHoursDayConfiguration) {
        this.day = dayConfig
        val view = View.inflate(context, R.layout.dk_layout_working_hours_day_item, null)
        cardView = view.findViewById(R.id.cardview)
        sliderContainer = view.findViewById(R.id.rangeslider_container)
        rangeSlider = view.findViewById(R.id.range_slider)
        checkbox = view.findViewById(R.id.checkbox_select_day)
        labelDay = view.findViewById(R.id.card_view_day_textview)
        labelMin = view.findViewById(R.id.textMin)
        labelMax = view.findViewById(R.id.textMax)

        checkbox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                listener?.onDayChecked(isChecked)
                manageCheckboxStyle(isChecked)
                manageSliderVisibility(isChecked)
            }
            isChecked = !dayConfig.entireDayOff
        }

        labelDay.apply {
            val cal = Calendar.getInstance(Locale.getDefault())
            cal[Calendar.DAY_OF_WEEK] = dayConfig.day.toDay()
            text = SimpleDateFormat("E", Locale.getDefault()).format(cal.time)
        }

        rangeSlider.apply {
            setValues(dayConfig.startTime, dayConfig.endTime)

            labelBehavior = LabelFormatter.LABEL_GONE
            trackActiveTintList = ColorStateList.valueOf(DriveKitUI.colors.secondaryColor())
            trackInactiveTintList = ColorStateList.valueOf(DriveKitUI.colors.neutralColor())
            thumbTintList = ColorStateList.valueOf(DriveKitUI.colors.primaryColor())
            addOnChangeListener(RangeSlider.OnChangeListener(fun(
                _: RangeSlider,
                _: Float,
                _: Boolean
            ) {
                manageMinSeparation()
                updateHoursLabels()
            }))
            addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: RangeSlider) {
                    // do nothing
                }

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    listener?.onHoursUpdated(slider.values[0], slider.values[1])
                }
            })
        }
        manageSliderVisibility(checkbox.isChecked)

        labelMin.setTextColor(DriveKitUI.colors.primaryColor())
        labelMax.setTextColor(DriveKitUI.colors.primaryColor())

        updateHoursLabels()

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }


    fun setListener(listener: WorkingHoursDayListener) {
        this.listener = listener
    }

    fun getConfig() =
        DKWorkingHoursDayConfiguration(
            day = day.day,
            entireDayOff = !checkbox.isChecked,
            startTime = rangeSlider.values[0],
            endTime = rangeSlider.values[1]
        )

    private fun manageCheckboxStyle(isChecked: Boolean) {
        CompoundButtonCompat.getButtonDrawable(checkbox)?.let { wrapped ->
            wrapped.mutate()
            val tintColor = if (isChecked) DriveKitUI.colors.secondaryColor() else DriveKitUI.colors.complementaryFontColor()
            DrawableCompat.setTint(wrapped, tintColor)
            checkbox.buttonDrawable = wrapped
        }
    }

    private fun manageSliderVisibility(display: Boolean) {
        val alpha = if (display) 1.0f else 0.15f
        sliderContainer.animate().alpha(alpha)
            .setDuration(context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    rangeSlider.isClickable = display
                    rangeSlider.isEnabled = display
                }
            })
    }

    // Fix minSeparation property that is not working
    // Taken from : https://github.com/material-components/material-components-android/issues/2147#issuecomment-817232225
    private fun manageMinSeparation() {
        val minValue = 0.0f
        val maxValue = 24.0f
        val minRange = 0.5f

        val from = rangeSlider.values[0]
        val to = rangeSlider.values[1]

        var newFrom = from
        var newTo = to

        if (to - from < minRange && newFrom > minValue) {
            newFrom = from - 1
        }
        if (newFrom + minRange > to && newTo < maxValue) {
            newTo = to + 1
        }
        if (newFrom == minValue && newTo - minRange < newFrom) {
            newTo = newFrom + minRange
        }
        if (newTo == maxValue && newFrom + minRange > newTo) {
            newFrom = newTo - minRange
        }
        rangeSlider.setValues(newFrom, newTo)
    }

    private fun updateHoursLabels() {
        labelMin.text = rawHoursValueToDate(rangeSlider.values[0])
        labelMax.text = rawHoursValueToDate(rangeSlider.values[1])
    }

    private fun rawHoursValueToDate(hours: Float): String? {
        val df: DateFormat = SimpleDateFormat(DKDatePattern.HOUR_MINUTE_LETTER.getPattern(), Locale.getDefault())
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal.add(Calendar.MINUTE, (60 * hours).toInt())
        return df.format(cal.time)
    }

    interface WorkingHoursDayListener {
        fun onDayChecked(checked: Boolean)
        fun onHoursUpdated(start: Float, end: Float)
    }
}