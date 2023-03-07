package com.drivequant.drivekit.ui.mysynthesis.component.community

import com.drivequant.drivekit.common.ui.utils.DKScoreTypeLevel
import com.drivequant.drivekit.core.extension.reduceAccuracy
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.driverdata.community.statistics.DKScoreStatistics
import com.drivequant.drivekit.ui.extension.getLevelForValue

internal class MySynthesisGaugeViewModel {

    var onUpdateCallback: (() -> Unit)? = null
    var onScoreDescriptionButtonClick: (() -> Unit)? = null

    var scoreValue: Double? = null
        private set
    var scoreLevel: DKScoreTypeLevel? = null
        private set
    var communityMinScore: Double? = null
        private set
    var communityMeanScore: Double? = null
        private set
    var communityMaxScore: Double? = null
        private set
    private var score: DKScoreType? = null

    fun configure(score: DKScoreType, scoreValue: Double?, communityMinScore: Double, communityMeanScore: Double, communityMaxScore: Double) {
        this.score = score
        this.scoreValue = scoreValue
        this.scoreLevel = scoreValue?.let { score.getLevelForValue(it) }
        this.communityMinScore = communityMinScore
        this.communityMeanScore = communityMeanScore
        this.communityMaxScore = communityMaxScore
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

    fun getPercent(scoreValue: Double, level0: Double, level7: Double): Float? {
        return if ((level7 - level0) > 0) {
            ((scoreValue.reduceAccuracy(1) - level0) / (level7 - level0)).toFloat()
        } else {
            null
        }
    }

}
