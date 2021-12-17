package com.drivequant.drivekit.tripanalysis.activationhours.viewholder

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
import com.drivequant.drivekit.tripanalysis.activationhours.toDay
import com.drivequant.drivekit.tripanalysis.activationhours.viewmodel.ActivationHoursViewModel
import com.drivequant.drivekit.tripanalysis.service.activationhours.DKActivationHoursDayConfiguration
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class ActivationHoursDayViewHolder(
    itemView: View,
    private val viewModel: ActivationHoursViewModel
) : RecyclerView.ViewHolder(itemView)
{
    private val sliderContainer = itemView.findViewById<RelativeLayout>(R.id.rangeslider_container)
    private val rangeSlider = itemView.findViewById<RangeSlider>(R.id.range_slider)
    private val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox_select_day)
    private val labelDay = itemView.findViewById<TextView>(R.id.card_view_day_textview)
    private val labelMin = itemView.findViewById<TextView>(R.id.textMin)
    private val labelMax = itemView.findViewById<TextView>(R.id.textMax)

    init {
        labelMin.setTextColor(DriveKitUI.colors.primaryColor())
        labelMax.setTextColor(DriveKitUI.colors.primaryColor())
        rangeSlider.labelBehavior = LabelFormatter.LABEL_GONE
        rangeSlider.trackActiveTintList = ColorStateList.valueOf(DriveKitUI.colors.secondaryColor())
        rangeSlider.trackInactiveTintList = ColorStateList.valueOf(DriveKitUI.colors.neutralColor())
        rangeSlider.addOnChangeListener(RangeSlider.OnChangeListener(fun(
            _: RangeSlider,
            _: Float,
            _: Boolean
        ) {
            manageMinSeparation()
            updateHoursLabels()
        }))
        rangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
                // do nothing
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
               updateDayConfig()
            }
        })

        checkBox.setOnCheckedChangeListener { _, _ ->
            manageSliderVisibility()
            updateDayConfig()
        }
    }

    fun bind(position: Int) {
        viewModel.config?.dayConfiguration?.get(position)?.let { dayConfig ->
            initDayLabel(dayConfig)
            checkBox.isChecked = !dayConfig.entireDayOff
            rangeSlider.setValues(dayConfig.startTime, dayConfig.endTime)
            updateHoursLabels()
            manageSliderVisibility()
        }
    }

    private fun updateDayConfig() {
        viewModel.config?.dayConfiguration?.get(adapterPosition)?.let { dayConfig ->
            val newDayConfig = DKActivationHoursDayConfiguration(
                day = dayConfig.day,
                entireDayOff = !checkBox.isChecked,
                reverse = false, // The current UI does not manage that feature yet
                startTime = rangeSlider.values[0],
                endTime = rangeSlider.values[1]
            )
            viewModel.updateDayConfig(newDayConfig)
        }
    }

    private fun manageSliderVisibility() {
        if (!checkBox.isChecked) {
            sliderContainer.animate().alpha(0.15f)
                .setDuration(
                    itemView.context.resources.getInteger(android.R.integer.config_shortAnimTime)
                        .toLong()
                )
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        rangeSlider.isClickable = false
                        rangeSlider.isEnabled = false
                    }
                })
        } else {
            sliderContainer.animate().alpha(1f)
                .setDuration(
                    itemView.context.resources.getInteger(android.R.integer.config_shortAnimTime)
                        .toLong()
                )
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        rangeSlider.isClickable = true
                        rangeSlider.isEnabled = true
                    }
                })
        }
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

    private fun initDayLabel(dayConfig: DKActivationHoursDayConfiguration) {
        val df: DateFormat = SimpleDateFormat("E", Locale.getDefault())
        val cal = Calendar.getInstance(Locale.getDefault())
        cal[Calendar.DAY_OF_WEEK] = dayConfig.day.toDay()
        labelDay.text = df.format(cal.time)
    }

    private fun updateHoursLabels() {
        labelMin.text = viewModel.rawHoursValueToDate(rangeSlider.values[0])
        labelMax.text = viewModel.rawHoursValueToDate(rangeSlider.values[1])
    }
}