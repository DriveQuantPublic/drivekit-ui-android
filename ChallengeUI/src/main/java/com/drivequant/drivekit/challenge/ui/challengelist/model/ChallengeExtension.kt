package com.drivequant.drivekit.challenge.ui.challengelist.model

import com.drivequant.drivekit.databaseutils.entity.Challenge
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus

fun Challenge.getChallengeState(): ChallengeState {
    return when (this.status) {
        ChallengeStatus.PENDING, ChallengeStatus.SCHEDULED -> {
            ChallengeState.ACTIVE
        }
        ChallengeStatus.FINISHED, ChallengeStatus.ARCHIVED -> {
            ChallengeState.COMPLETE
        }
        else -> {
            ChallengeState.UNDERWAY
        }
    }
}