package com.drivequant.drivekit.common.ui.component.dateselector

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.common.DKPeriod
import java.util.*

class DKDateSelectorViewModel : ViewModel() {

    var listener: DKDateSelectorListener? = null

    lateinit var period: DKPeriod
        private set
    private lateinit var dates: List<Date>
    private var selectedDateIndex: Int = -1
    var hasPreviousDate = false
        private set
    var hasNextDate = false
        private set

    lateinit var fromDate: Date
    lateinit var toDate: Date

    fun configure(dates: List<Date>, selectedDateIndex: Int?, period: DKPeriod) {
        this.dates = dates
        this.period = period
        this.selectedDateIndex = selectedDateIndex ?: -1
        updateProperties()
    }

    private fun updateProperties() {
        if (selectedDateIndex > -1) {
            hasNextDate = dates.count() > selectedDateIndex + 1
            hasPreviousDate = selectedDateIndex > 0
            fromDate = dates[selectedDateIndex]
            toDate = getEndDate(fromDate, period)
        }
    }

    fun hasDates() = this::dates.isInitialized && dates.isNotEmpty()

    fun moveToPreviousDate() {
        if (hasPreviousDate) {
            selectedDateIndex--
            updateProperties()
            listener?.onDateSelected(fromDate)
        }
    }

    fun moveToNextDate() {
        if (hasNextDate) {
            selectedDateIndex++
            updateProperties()
            listener?.onDateSelected(fromDate)
        }
    }

    private fun getEndDate(fromDate: Date, period: DKPeriod): Date {
        val calendar = Calendar.getInstance()
        calendar.time = fromDate
        when (period) {
            DKPeriod.WEEK -> calendar.add(Calendar.DATE, 6)
            DKPeriod.MONTH -> {
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
            DKPeriod.YEAR -> {
                calendar.add(Calendar.YEAR, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
        }
        return calendar.time
    }
}
