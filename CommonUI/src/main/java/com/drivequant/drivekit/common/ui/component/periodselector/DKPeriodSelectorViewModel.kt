package com.drivequant.drivekit.common.ui.component.periodselector

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.common.DKPeriod

class DKPeriodSelectorViewModel: ViewModel() {

    var listener: DKPeriodSelectorItemListener? = null
    var selectedPeriod: DKPeriod = DKPeriod.WEEK
        private set

    fun configure(selectedPeriod: DKPeriod) {
        if (this.selectedPeriod != selectedPeriod) {
            this.selectedPeriod = selectedPeriod
            onPeriodSelected(selectedPeriod)
        }
    }

    internal fun onPeriodSelected(period: DKPeriod) {
        if (this.selectedPeriod != period) {
            this.selectedPeriod = period
            listener?.onPeriodSelected(period)
        }
    }

}
