package com.drivequant.drivekit.timeline.ui.component.periodselector

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod

internal class PeriodSelectorViewModel: ViewModel() {

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
}