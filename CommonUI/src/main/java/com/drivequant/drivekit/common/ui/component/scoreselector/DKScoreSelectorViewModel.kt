package com.drivequant.drivekit.common.ui.component.scoreselector

import com.drivequant.drivekit.core.scoreslevels.DKScoreType

class DKScoreSelectorViewModel {

    var scores: List<DKScoreType> = DKScoreType.values().toList()
        private set(value) {
            field = value.ifEmpty {
                listOf(DKScoreType.SAFETY)
            }.filter { it.hasAccess() }
            if (field.isNotEmpty() && !field.contains(this.selectedScore)) {
                this.selectedScore = field.first()
            }
        }
    var selectedScore: DKScoreType = this.scores.first()
        private set
    private var onScoreChangeCallback: ((score: DKScoreType) -> Unit)? = null

    fun configure(scores: List<DKScoreType>, onScoreChange: (score: DKScoreType) -> Unit) {
        this.scores = scores
        this.onScoreChangeCallback = onScoreChange
    }

    internal fun onScoreChange(score: DKScoreType) {
        if (this.selectedScore != score) {
            this.selectedScore = score
            this.onScoreChangeCallback?.invoke(score)
        }
    }

}
