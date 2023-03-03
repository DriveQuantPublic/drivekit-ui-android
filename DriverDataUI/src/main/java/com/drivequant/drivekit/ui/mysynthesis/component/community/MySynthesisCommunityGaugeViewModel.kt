package com.drivequant.drivekit.ui.mysynthesis.component.community

import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.driverdata.community.statistics.DKScoreStatistics

internal class MySynthesisCommunityGaugeViewModel {

    var onUpdateCallback: (() -> Unit)? = null

    var scoreValue: Double? = null
        private set
    var communityMinScore: Double? = null
        private set
    var communityMeanScore: Double? = null
        private set
    var communityMaxScore: Double? = null
        private set
    private var score: DKScoreType? = null
    private var percentiles: List<Double>? = null

    fun configure(score: DKScoreType, scoreValue: Double?, scoreStatistics: DKScoreStatistics) {
        this.score = score
        this.scoreValue = scoreValue
        this.communityMinScore = scoreStatistics.min
        this.communityMeanScore = scoreStatistics.mean
        this.communityMaxScore = scoreStatistics.max
        this.percentiles = scoreStatistics.percentiles
        this.onUpdateCallback?.invoke()
    }

    fun getLevelValue(index: Int): Double? {
        val steps = this.score?.getSteps()
        return if (steps == null || index < 0 || index >= steps.size) {
            null
        } else {
            steps[index]
        }
    }

}
