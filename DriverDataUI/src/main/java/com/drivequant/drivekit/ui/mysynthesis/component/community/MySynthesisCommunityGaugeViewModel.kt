package com.drivequant.drivekit.ui.mysynthesis.component.community

import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.driverdata.community.statistics.DKScoreStatistics

internal class MySynthesisCommunityGaugeViewModel {

    var onUpdateCallback: (() -> Unit)? = null

    var scoreValue: Double? = null
        private set
    private var score: DKScoreType? = null
    private var scoreStatistics: DKScoreStatistics? = null

    fun configure(score: DKScoreType, scoreValue: Double?, scoreStatistics: DKScoreStatistics) {
        this.score = score
        this.scoreValue = scoreValue
        this.scoreStatistics = scoreStatistics
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

    fun getCommunityMinScore(): Double? = this.scoreStatistics?.min
    fun getCommunityMeanScore(): Double? = this.scoreStatistics?.mean
    fun getCommunityMaxScore(): Double? = this.scoreStatistics?.max

}
