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
    private var selectedDateIndex: Int? = null
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
        selectedDateIndex?.let {
            hasNextDate = dates.count() > it + 1
            hasPreviousDate = it > 0
            fromDate = dates[it]
            toDate = getEndDate(fromDate, period)
        }
    }

    private fun moveToNextDate() {

    }

    private fun getEndDate(fromDate: String, period: DKTimelinePeriod): String {
        val backendDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val computedFromDate = backendDateFormat.parse(fromDate)
        val calendar = Calendar.getInstance()
        calendar.time = computedFromDate
        when (period) {
            DKTimelinePeriod.WEEK -> calendar.add(Calendar.DAY_OF_YEAR, 6)
            DKTimelinePeriod.MONTH ->{
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
        }
        Log.d("DEBUG", "fromDate is : $fromDate, period is : $period, computed endDate is : $calendar")
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
