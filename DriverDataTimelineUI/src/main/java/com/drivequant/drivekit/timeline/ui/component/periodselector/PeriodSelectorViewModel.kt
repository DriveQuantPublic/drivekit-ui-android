package com.drivequant.drivekit.timeline.ui.component.periodselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod

class PeriodSelectorViewModel: ViewModel() {

    var listener: PeriodSelectorItemListener? = null
    var selectedPeriod: DKTimelinePeriod = DKTimelinePeriod.WEEK
        private set

    fun configure(selectedPeriod: DKTimelinePeriod) {
        if (this.selectedPeriod != selectedPeriod) {
            this.selectedPeriod = selectedPeriod
            onPeriodSelected(selectedPeriod)
        }
    }

    fun onPeriodSelected(period: DKTimelinePeriod) {
        if (this.selectedPeriod != period) {
            this.selectedPeriod = period
            listener?.onPeriodSelected(period)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class PeriodSelectorViewModelFactory :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PeriodSelectorViewModel() as T
        }
    }
}