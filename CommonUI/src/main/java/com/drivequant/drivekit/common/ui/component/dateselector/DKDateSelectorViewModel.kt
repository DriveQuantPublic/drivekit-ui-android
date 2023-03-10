package com.drivequant.drivekit.common.ui.component.dateselector

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.add
import com.drivequant.drivekit.core.extension.removeTime
import com.drivequant.drivekit.core.extension.startingFrom
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import java.util.*

class DKDateSelectorViewModel : ViewModel() {

    var onDateSelected: ((date: Date) -> Unit)? = null

    var period: DKPeriod? = null
        private set
    private var dates: List<Date>? = null
    private var selectedDateIndex: Int = -1
    var hasPreviousDate = false
        private set
    var hasNextDate = false
        private set

    var fromDate: Date? = null
        private set
    var toDate: Date? = null
        private set

    fun configure(dates: List<Date>, selectedDateIndex: Int?, period: DKPeriod) {
        this.dates = dates
        this.period = period
        this.selectedDateIndex = selectedDateIndex ?: -1
        updateProperties()
    }

    private fun updateProperties() {
        if (selectedDateIndex > -1) {
            val dates = this.dates
            val period = this.period
            if (dates != null && period != null) {
                hasNextDate = dates.count() > selectedDateIndex + 1
                hasPreviousDate = selectedDateIndex > 0
                val fromDate = dates[selectedDateIndex]
                this.fromDate = fromDate
                toDate = getEndDate(fromDate, period)
            }
        }
    }

    fun hasDates() = this.dates?.isNotEmpty() ?: false

    fun moveToPreviousDate() {
        if (hasPreviousDate) {
            selectedDateIndex--
            updateProperties()
            fromDate?.let {
                onDateSelected?.invoke(it)
            }
        }
    }

    fun moveToNextDate() {
        if (hasNextDate) {
            selectedDateIndex++
            updateProperties()
            fromDate?.let {
                onDateSelected?.invoke(it)
            }
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

    companion object {
        fun newSelectedDate(
            date: Date,
            period: DKPeriod,
            dates: List<Date>,
            isDateValid: (DKPeriod, Date) -> Boolean
        ): Date {
            val calendarField = when (period) {
                DKPeriod.WEEK -> CalendarField.WEEK
                DKPeriod.MONTH -> CalendarField.MONTH
                DKPeriod.YEAR -> CalendarField.YEAR
            }
            val compareDate: Date =
                date.startingFrom(calendarField).add(1, calendarField).add(-1, CalendarField.DAY)
            return dates.lastOrNull {
                it.removeTime() <= compareDate && isDateValid(period, it)
            } ?: date
        }
    }

}
