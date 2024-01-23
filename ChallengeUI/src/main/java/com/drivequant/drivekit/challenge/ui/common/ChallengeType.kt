package com.drivequant.drivekit.challenge.ui.common

import com.drivequant.drivekit.databaseutils.entity.Challenge

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// TODO: THIS CLASS NEEDS TO BE REMOVED. USE THE ONE IN INTERNAL MODULES INSTEAD.
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
enum class ChallengeType {
    SAFETY,
    ECODRIVING,
    DISTRACTION,
    SPEEDING,
    HARD_BRAKING,
    HARD_ACCELERATION,
    DEPRECATED,
    UNKNOWN;

    companion object {
        internal fun fromThemeCode(themeCode: Int): ChallengeType = when (themeCode) {
            in 101..104 -> ECODRIVING
            in 201..204 -> SAFETY
            in 205..208 -> HARD_BRAKING
            in 209..212 -> HARD_ACCELERATION
            in 213..220 -> SAFETY
            in 301..309 -> DEPRECATED
            221 -> DISTRACTION
            401 -> SPEEDING
            else -> UNKNOWN
        }
    }
}

fun Challenge.challengeType(): ChallengeType = ChallengeType.fromThemeCode(themeCode)
