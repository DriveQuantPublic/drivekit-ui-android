package com.drivequant.drivekit.ui.extension

import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.utils.DKScoreTypeLevel
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.ui.R

@StringRes
internal fun DKScoreTypeLevel.getScoreLevelTitle() = when (this) {
    DKScoreTypeLevel.EXCELLENT -> R.string.dk_driverdata_mysynthesis_score_title_excellent
    DKScoreTypeLevel.VERY_GOOD -> R.string.dk_driverdata_mysynthesis_score_title_very_good
    DKScoreTypeLevel.GREAT -> R.string.dk_driverdata_mysynthesis_score_title_good
    DKScoreTypeLevel.MEDIUM -> R.string.dk_driverdata_mysynthesis_score_title_average
    DKScoreTypeLevel.NOT_GOOD -> R.string.dk_driverdata_mysynthesis_score_title_low
    DKScoreTypeLevel.BAD -> R.string.dk_driverdata_mysynthesis_score_title_bad
    DKScoreTypeLevel.VERY_BAD -> R.string.dk_driverdata_mysynthesis_score_title_very_bad
}

@StringRes
internal fun DKScoreTypeLevel.getScoreLevelDescription(dkScoreType: DKScoreType) = when (this) {
    DKScoreTypeLevel.EXCELLENT -> when (dkScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_excellent
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_excellent
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_excellent
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_excellent
    }
    DKScoreTypeLevel.VERY_GOOD -> when (dkScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_very_good
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_very_good
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_very_good
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_very_good
    }
    DKScoreTypeLevel.GREAT -> when (dkScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_good
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_good
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_good
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_good
    }
    DKScoreTypeLevel.MEDIUM -> when (dkScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_average
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_average
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_average
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_average
    }
    DKScoreTypeLevel.NOT_GOOD -> when (dkScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_low
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_low
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_low
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_low
    }
    DKScoreTypeLevel.BAD -> when (dkScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_bad
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_bad
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_bad
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_bad
    }
    DKScoreTypeLevel.VERY_BAD -> when (dkScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_very_bad
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_very_bad
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_very_bad
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_very_bad
    }
}


@StringRes
internal fun DKScoreTypeLevel.getScoreLevelDescription() = when (this) {
    DKScoreTypeLevel.EXCELLENT -> R.string.dk_driverdata_mysynthesis_excellent_score
    DKScoreTypeLevel.VERY_GOOD -> R.string.dk_driverdata_mysynthesis_very_good_score
    DKScoreTypeLevel.GREAT -> R.string.dk_driverdata_mysynthesis_good_score
    DKScoreTypeLevel.MEDIUM -> R.string.dk_driverdata_mysynthesis_average_score
    DKScoreTypeLevel.NOT_GOOD -> R.string.dk_driverdata_mysynthesis_low_score
    DKScoreTypeLevel.BAD -> R.string.dk_driverdata_mysynthesis_bad_score
    DKScoreTypeLevel.VERY_BAD -> R.string.dk_driverdata_mysynthesis_very_bad_score
}

internal fun DKScoreType.getLevelForValue(value: Double): DKScoreTypeLevel? {
    for (level in DKScoreTypeLevel.values()) {
        val levels = level.getScoreLevel(this)
        if (value >= levels.first && value <= levels.second) {
            return level
        }
    }
    return null
}
