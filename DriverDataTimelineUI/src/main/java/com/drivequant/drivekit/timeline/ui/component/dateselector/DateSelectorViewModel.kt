package com.drivequant.drivekit.timeline.ui.component.dateselector

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateSelectorViewModel : ViewModel() {

    var listener: DateSelectorListener? = null

    private lateinit var dates: List<String>
    private var selectedDateIndex: Int = -1
    private lateinit var period: DKTimelinePeriod
    var hasPreviousDate = false
        private set
    var hasNextDate = false
        private set

    lateinit var fromDate: String
    lateinit var toDate: String

    fun configure(dates: List<String>, selectedDateIndex: Int?, period: DKTimelinePeriod) {
        this.dates = dates
        this.period = period
        selectedDateIndex?.let { // TODO dirty
            this.selectedDateIndex = selectedDateIndex
        }
        // TODO listener
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
            selectedDateIndex -= 1
            updateProperties()
        } else {
            Log.d("DEBUG", "No previous date !") //TODO remove debug log
        }
    }


    fun moveToNextDate() {
        if (hasNextDate) {
            selectedDateIndex += 1
            updateProperties()
        } else {
            Log.d("DEBUG", "No next date !") //TODO remove debug log
        }
    }

    private fun getEndDate(fromDate: String, period: DKTimelinePeriod): String {
        val calendar = Calendar.getInstance()
        val backendDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        backendDateFormat.timeZone = TimeZone.getTimeZone("GMT")
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
        return calendar.toString()
    }

    @Suppress("UNCHECKED_CAST")
    class DateSelectorViewModelFactory :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DateSelectorViewModel() as T
        }
    }
}
