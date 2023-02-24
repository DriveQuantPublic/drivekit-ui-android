package com.drivequant.drivekit.ui.mysynthesis

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.component.periodselector.DKPeriodSelectorViewModel
import com.drivequant.drivekit.common.ui.component.scoreselector.DKScoreSelectorViewModel
import com.drivequant.drivekit.core.common.DKPeriod
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.ui.DriverDataUI

internal class MySynthesisViewModel(application: Application) : AndroidViewModel(application) {

    private val scores = DriverDataUI.scores
    private val periods = listOf(DKPeriod.WEEK, DKPeriod.MONTH, DKPeriod.YEAR)
    val periodSelectorViewModel = DKPeriodSelectorViewModel()
    val scoreSelectorViewModel = DKScoreSelectorViewModel()
    private var selectedScore: DKScoreType
    private var selectedPeriod: DKPeriod = this.periods.first()

    init {
        this.selectedScore = this.scores.firstOrNull() ?: DKScoreType.SAFETY
        configureScoreSelector()
        configurePeriodSelector()
        update()
    }

    private fun update() {
        Log.d("TEST", "=== selectedScore = $selectedScore - selectedPeriod = $selectedPeriod")
    }

    private fun configureScoreSelector() {
        this.scoreSelectorViewModel.configure(this.scores) { score ->
            if (this.selectedScore != score) {
                this.selectedScore = score
                update()
            }
        }
    }

    private fun configurePeriodSelector() {
        this.periodSelectorViewModel.configure(periods)
        this.periodSelectorViewModel.onPeriodSelected = { period ->
            if (this.selectedPeriod != period) {
                this.selectedPeriod = period
                update()
            }
        }
    }

    class MySynthesisViewModelFactory(private val application: Application) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MySynthesisViewModel(application) as T
        }
    }

}
