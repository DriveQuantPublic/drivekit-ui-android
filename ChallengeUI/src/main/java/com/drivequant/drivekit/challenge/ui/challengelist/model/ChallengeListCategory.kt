package com.drivequant.drivekit.challenge.ui.challengelist.model

import androidx.annotation.StringRes
import com.drivequant.drivekit.challenge.ui.R

enum class ChallengeListCategory {
    ACTIVE,
    RANKED,
    ALL;

    @StringRes
    fun getText(): Int = when (this) {
        ACTIVE -> R.string.dk_challenge_active
        RANKED -> R.string.dk_challenge_ranked
        ALL -> R.string.dk_challenge_all
    }
}