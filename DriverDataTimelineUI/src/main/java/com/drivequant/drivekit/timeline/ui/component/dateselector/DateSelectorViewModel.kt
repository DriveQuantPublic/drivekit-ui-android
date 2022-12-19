package com.drivequant.drivekit.timeline.ui.component.dateselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import java.util.*

internal class DateSelectorViewModel : ViewModel() {

    var listener: DateSelectorListener? = null

    lateinit var period: DKTimelinePeriod
        private set
    private lateinit var dates: List<Date>
    private var selectedDateIndex: Int = -1
    var hasPreviousDate = false
        private set
    var hasNextDate = false
        private set

    lateinit var fromDate: Date
    lateinit var toDate: Date

    fun configure(dates: List<Date>, selectedDateIndex: Int?, period: DKTimelinePeriod) {
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
            listener?.onDateSelected(fromDate)
        }
    }

    fun hasDates() = this::dates.isInitialized && dates.isNotEmpty()

    fun moveToPreviousDate() {
        if (hasPreviousDate) {
            selectedDateIndex--
            updateProperties()
        }
    }

    fun moveToNextDate() {
        if (hasNextDate) {
            selectedDateIndex++
            updateProperties()
        }
    }

    private fun getEndDate(fromDate: Date, period: DKTimelinePeriod): Date {
        val calendar = Calendar.getInstance()
        calendar.time = fromDate
        when (period) {
            DKTimelinePeriod.WEEK -> calendar.add(Calendar.DATE, 6)
            DKTimelinePeriod.MONTH -> {
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
        }
        return calendar.time
    }

    @Suppress("UNCHECKED_CAST")
    class DateSelectorViewModelFactory :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DateSelectorViewModel() as T
        }
    }
}
