package com.drivequant.drivekit.tripanalysis.activationhours.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.tripanalysis.activationhours.viewmodel.ActivationHoursViewModel
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class ActivationHoursDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val rangeSlider = itemView.findViewById<RangeSlider>(R.id.range_slider)
    private val labelDay = itemView.findViewById<TextView>(R.id.card_view_day_textview)
    private val labelMin = itemView.findViewById<TextView>(R.id.textMin)
    private val labelMax = itemView.findViewById<TextView>(R.id.textMax)

    init {
        updateHoursLabels()
        rangeSlider.labelBehavior = LabelFormatter.LABEL_GONE
        rangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {

            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                // do nothing
            }
        })

        rangeSlider.addOnChangeListener(object : RangeSlider.OnChangeListener {
            override fun onValueChange(slider: RangeSlider, value: Float, fromUser: Boolean) {
                updateHoursLabels()
            }
        })
    }

    fun bind(dayConfig : ActivationHoursViewModel.DKDay) {
        initDayLabel(dayConfig)
    }

    private fun initDayLabel(dayConfig: ActivationHoursViewModel.DKDay) {
        val df: DateFormat = SimpleDateFormat("E", Locale.getDefault())
        val cal = Calendar.getInstance(Locale.getDefault())
        //cal[Calendar.DAY_OF_WEEK] = dayConfig.mDayOfWeek
        cal[Calendar.DAY_OF_WEEK] = 0
        labelDay.text = df.format(cal.time)
    }

    private fun updateHoursLabels() {
        val values = rangeSlider.values
        if (values.size == 2) {
            // update min and max label
            labelMin.text = rawValueToDateConverter(values[0])
            labelMax.text = rawValueToDateConverter(values[1])
        }
    }

    //TODO: in viewmodel
    private fun rawValueToDateConverter(tick: Float): String? {
        val MINUTES_BY_TICK = 60
        val df: DateFormat = SimpleDateFormat("HH'h'mm", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val test = (MINUTES_BY_TICK * tick).toInt()
        cal.add(Calendar.MINUTE, test)
        return df.format(cal.time)
    }
}