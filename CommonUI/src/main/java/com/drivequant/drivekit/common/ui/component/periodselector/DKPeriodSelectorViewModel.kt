package com.drivequant.drivekit.common.ui.component.periodselector

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.databaseutils.entity.DKPeriod

class DKPeriodSelectorViewModel: ViewModel() {

    var onPeriodSelected: ((oldPeriod: DKPeriod, newPeriod: DKPeriod) -> Unit)? = null
    var periods: List<DKPeriod> = listOf(DKPeriod.WEEK, DKPeriod.MONTH, DKPeriod.YEAR)
        private set
    var selectedPeriod: DKPeriod = this.periods.last()
        private set

    fun configure(periods: List<DKPeriod>) {
        this.periods = periods
        if (periods.isNotEmpty() && !periods.contains(this.selectedPeriod)) {
            this.selectedPeriod = periods.last()
        }
    }

    fun select(period: DKPeriod) {
        this.selectedPeriod = period
    }

    internal fun onPeriodSelected(period: DKPeriod) {
        if (this.selectedPeriod != period) {
            val oldPeriod = this.selectedPeriod
            select(period)
            onPeriodSelected?.invoke(oldPeriod, period)
        }
    }

}
