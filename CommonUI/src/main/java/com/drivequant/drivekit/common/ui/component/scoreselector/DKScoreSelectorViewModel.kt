package com.drivequant.drivekit.common.ui.component.scoreselector

import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.core.scoreslevels.DKScoreType

class DKScoreSelectorViewModel {

    private var internalScores: List<DKScoreType> = DriveKitUI.scores
    var scores: List<DKScoreType>
        get() {
            return this.internalScores.filter { it.hasAccess() }
        }
        private set(value) {
            this.internalScores = value.ifEmpty {
                listOf(DKScoreType.SAFETY)
            }
            this.selectedScore = this.internalScores.first()
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
