package com.drivequant.drivekit.common.ui.utils

import androidx.annotation.ColorRes
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.core.scoreslevels.DKScoreType

enum class DKScoreTypeLevel {
    EXCELLENT, VERY_GOOD, GREAT, MEDIUM, NOT_GOOD, BAD, VERY_BAD;

    @ColorRes
    fun getColorResId() = when (this) {
        EXCELLENT -> R.color.dkExcellent
        VERY_GOOD -> R.color.dkGood
        GREAT -> R.color.dkGoodMean
        MEDIUM -> R.color.dkMean
        NOT_GOOD -> R.color.dkBadMean
        BAD -> R.color.dkBad
        VERY_BAD -> R.color.dkVeryBad
    }

    fun getScoreLevel(dkScoreType: DKScoreType): Pair<Double, Double> {
        val steps = dkScoreType.getSteps()
        return when (this) {
            EXCELLENT -> Pair(steps[6], steps[7])
            VERY_GOOD -> Pair(steps[5], steps[6])
            GREAT -> Pair(steps[4], steps[5])
            MEDIUM -> Pair(steps[3], steps[4])
            NOT_GOOD -> Pair(steps[2], steps[3])
            BAD -> Pair(steps[1], steps[2])
            VERY_BAD -> Pair(steps[0], steps[1])
        }
    }
}