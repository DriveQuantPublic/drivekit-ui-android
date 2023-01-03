package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal class TimelineDetailViewModel(application: Application): AndroidViewModel(application) {



    @Suppress("UNCHECKED_CAST")
    class TimelineDetailViewModelFactory(private val application: Application) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimelineDetailViewModel(application) as T
        }
    }
}