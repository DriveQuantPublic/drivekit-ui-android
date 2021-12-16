package com.drivequant.drivekit.tripanalysis.activationhours.viewholder

import android.content.res.ColorStateList
import android.view.View
import android.widget.CheckBox
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
        rangeSlider.trackInactiveTintList = ColorStateList.valueOf(DriveKitUI.colors.primaryColor())
        rangeSlider.addOnChangeListener(RangeSlider.OnChangeListener(fun(
            _: RangeSlider,
            _: Float,
            _: Boolean
        ) {
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
            updateDayConfig()
        }
    }

    fun bind(position: Int) {
        viewModel.config?.dayConfiguration?.get(position)?.let { dayConfig ->
            initDayLabel(dayConfig)
            checkBox.isChecked = !dayConfig.entireDayOff
            rangeSlider.setValues(dayConfig.startTime, dayConfig.endTime)
            updateHoursLabels()
        }
    }

    fun updateDayConfig() {
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

    private fun initDayLabel(dayConfig: DKActivationHoursDayConfiguration) {
        val df: DateFormat = SimpleDateFormat("E", Locale.getDefault())
        val cal = Calendar.getInstance(Locale.getDefault())
        cal[Calendar.DAY_OF_WEEK] = dayConfig.day.toDay()
        labelDay.text = df.format(cal.time)
    }

    private fun updateHoursLabels() {
        labelMin.text = viewModel.rawTickValueToDate(rangeSlider.values[0])
        labelMax.text = viewModel.rawTickValueToDate(rangeSlider.values[1])
    }
}