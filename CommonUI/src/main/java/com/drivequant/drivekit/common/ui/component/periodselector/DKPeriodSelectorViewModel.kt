package com.drivequant.drivekit.common.ui.component.periodselector

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.common.DKPeriod

class DKPeriodSelectorViewModel: ViewModel() {

    var onPeriodSelected: ((period: DKPeriod) -> Unit)? = null
    var periods: List<DKPeriod> = listOf(DKPeriod.WEEK, DKPeriod.MONTH, DKPeriod.YEAR)
        private set
    var selectedPeriod: DKPeriod = DKPeriod.WEEK
        private set

    fun configure(periods: List<DKPeriod>) {
        this.periods = periods
    }

    fun select(period: DKPeriod) {
        if (this.selectedPeriod != period) {
            this.selectedPeriod = period
        }
    }

    internal fun onPeriodSelected(period: DKPeriod) {
        if (this.selectedPeriod != period) {
            select(period)
            onPeriodSelected?.invoke(period)
        }
    }

}
