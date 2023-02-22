package com.drivequant.drivekit.timeline.ui.component.periodselector

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.common.DKPeriod

internal class PeriodSelectorViewModel: ViewModel() {

    var listener: PeriodSelectorItemListener? = null
    var selectedPeriod: DKPeriod = DKPeriod.WEEK
        private set

    fun configure(selectedPeriod: DKPeriod) {
        if (this.selectedPeriod != selectedPeriod) {
            this.selectedPeriod = selectedPeriod
            onPeriodSelected(selectedPeriod)
        }
    }

    fun onPeriodSelected(period: DKPeriod) {
        if (this.selectedPeriod != period) {
            this.selectedPeriod = period
            listener?.onPeriodSelected(period)
        }
    }
}
