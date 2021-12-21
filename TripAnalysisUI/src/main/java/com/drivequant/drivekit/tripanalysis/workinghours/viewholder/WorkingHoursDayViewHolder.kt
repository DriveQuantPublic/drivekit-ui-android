package com.drivequant.drivekit.tripanalysis.workinghours.viewholder

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.ColorStateList
import android.view.View
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.tripanalysis.workinghours.viewmodel.WorkingHoursViewModel
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHoursDayConfiguration
import com.drivequant.drivekit.tripanalysis.workinghours.toDay
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import java.text.SimpleDateFormat
import java.util.*

internal class WorkingHoursDayViewHolder(
    itemView: View,
    private val viewModel: WorkingHoursViewModel
) : RecyclerView.ViewHolder(itemView) {
    private val sliderContainer = itemView.findViewById<RelativeLayout>(R.id.rangeslider_container)
    private val rangeSlider = itemView.findViewById<RangeSlider>(R.id.range_slider)
    private val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox_select_day)
    private val labelDay = itemView.findViewById<TextView>(R.id.card_view_day_textview)
    private val labelMin = itemView.findViewById<TextView>(R.id.textMin)
    private val labelMax = itemView.findViewById<TextView>(R.id.textMax)

    init {
        labelMin.setTextColor(DriveKitUI.colors.primaryColor())
        labelMax.setTextColor(DriveKitUI.colors.primaryColor())
        rangeSlider.apply {
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
                    updateDayConfig()
                }
            })
        }
    }

    fun bind(position: Int) {
        viewModel.config?.dayConfiguration?.get(position)?.let { dayConfig ->
            initDayLabel(dayConfig)
            checkBox.isChecked = !dayConfig.entireDayOff
            rangeSlider.setValues(dayConfig.startTime, dayConfig.endTime)
            updateHoursLabels()
            manageSliderVisibility(checkBox.isChecked)
        }

        checkBox.setOnCheckedChangeListener { _, _ ->
            manageSliderVisibility(checkBox.isChecked)
            updateDayConfig()
        }
    }

    private fun updateDayConfig() {
        viewModel.config?.dayConfiguration?.get(adapterPosition)?.let { dayConfig ->
            val newDayConfig = DKWorkingHoursDayConfiguration(
                day = dayConfig.day,
                entireDayOff = !checkBox.isChecked,
                reverse = false, // The current UI does not manage that feature yet
                startTime = rangeSlider.values[0],
                endTime = rangeSlider.values[1]
            )
            viewModel.updateDayConfig(newDayConfig)
        }
    }

    private fun manageSliderVisibility(display: Boolean) {
        val alpha = if (display) 1.0f else 0.15f
        sliderContainer.animate().alpha(alpha)
            .setDuration(
                itemView.context.resources.getInteger(android.R.integer.config_shortAnimTime)
                    .toLong()
            )
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

    private fun initDayLabel(dayConfig: DKWorkingHoursDayConfiguration) {
        val cal = Calendar.getInstance(Locale.getDefault())
        cal[Calendar.DAY_OF_WEEK] = dayConfig.day.toDay()
        labelDay.text = SimpleDateFormat("E", Locale.getDefault()).format(cal.time)
    }

    private fun updateHoursLabels() {
        labelMin.text = viewModel.rawHoursValueToDate(rangeSlider.values[0])
        labelMax.text = viewModel.rawHoursValueToDate(rangeSlider.values[1])
    }
}