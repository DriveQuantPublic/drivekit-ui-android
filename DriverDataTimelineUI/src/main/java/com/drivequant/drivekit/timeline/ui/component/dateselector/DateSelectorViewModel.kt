package com.drivequant.drivekit.timeline.ui.component.dateselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateSelectorViewModel : ViewModel() {

    companion object {
        fun getBackendDateFormat(): DateFormat {
            val backendDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            backendDateFormat.timeZone = TimeZone.getTimeZone("GMT")
            return backendDateFormat
        }
    }

    var listener: DateSelectorListener? = null

    private lateinit var period: DKTimelinePeriod
    private lateinit var dates: List<String>
    private var selectedDateIndex: Int = -1
    var hasPreviousDate = false
        private set
    var hasNextDate = false
        private set

    private lateinit var fromDate: String
    private lateinit var toDate: String

    var computedFromDate: Date = Date()
        get() =
            if (this::fromDate.isInitialized) {
                getBackendDateFormat().parse(fromDate) ?: Date()
            } else {
                Date()
            }
        private set
    var computedToDate: Date = Date()
        get() =
            if (this::toDate.isInitialized) {
                getBackendDateFormat().parse(toDate) ?: Date()
            } else {
                Date()
            }
        private set

    fun configure(dates: List<String>, selectedDateIndex: Int?, period: DKTimelinePeriod) {
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

    private fun getEndDate(fromDate: String, period: DKTimelinePeriod): String {
        val calendar = Calendar.getInstance()
        val backendDateFormat = getBackendDateFormat()
        backendDateFormat.parse(fromDate)?.let { computedFromDate ->
            calendar.time = computedFromDate
            when (period) {
                DKTimelinePeriod.WEEK -> calendar.add(Calendar.DATE, 6)
                DKTimelinePeriod.MONTH -> {
                    calendar.add(Calendar.MONTH, 1)
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                }
            }
        }
        return backendDateFormat.format(calendar.time)
    }

    @Suppress("UNCHECKED_CAST")
    class DateSelectorViewModelFactory :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DateSelectorViewModel() as T
        }
    }
}
