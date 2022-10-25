package com.drivequant.drivekit.timeline.ui.component.dateselector

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*

class DateSelectorViewModel(
    private val dates: List<String>
) : ViewModel() {

    private val test = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n")
    var listener: DateSelectorListener? = null
    var hasPreviousDate: MutableLiveData<Boolean> = MutableLiveData()
    var hasNextDate: MutableLiveData<Boolean> = MutableLiveData()
    var currentIndex = 0
    val dateRange: MutableLiveData<Pair<String, String>> = MutableLiveData()

    init {
        val fromDate = test[currentIndex]
        val toDate = test[currentIndex + 1]
        dateRange.postValue(Pair(fromDate, toDate))
        hasNextDate.postValue(true)
        hasPreviousDate.postValue(false)
        currentIndex++
    }

    fun updateDate(directionType: DirectionType) {
        when (directionType) {
            DirectionType.NEXT -> next()
            DirectionType.PREVIOUS -> previous()
        }
    }

    private fun next() {
        hasPreviousDate.postValue(true)
        hasNextDate.postValue(false)
        /*if (currentIndex == 0) {
            currentIndex++
        }
        if (test.lastIndex > currentIndex) {
            val fromDate = test[currentIndex]
            val toDate = test[++currentIndex]
            dateRange.postValue(Pair(fromDate, toDate))
        } else {
            hasNextDate.postValue(false)
        }

         */
    }

    private fun previous() {
        hasNextDate.postValue(true)
        hasPreviousDate.postValue(false)
        /*if (currentIndex > 2) {
            val toDate = test[currentIndex - 1]
            val fromDate = test[currentIndex - 2]
            dateRange.postValue(Pair(fromDate, toDate))


        } else {

        }

         */
    }

    @Suppress("UNCHECKED_CAST")
    class DateSelectorViewModelFactory(private val dates: List<String>) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DateSelectorViewModel(dates) as T
        }
    }
}

enum class DirectionType {
    NEXT, PREVIOUS
}