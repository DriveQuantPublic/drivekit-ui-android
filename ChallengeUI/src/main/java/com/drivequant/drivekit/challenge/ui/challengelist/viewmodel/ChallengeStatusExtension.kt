package com.drivequant.drivekit.challenge.ui.challengelist.viewmodel

import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus

fun List<ChallengeStatus>.toStringArray(): Array<String> = this.map { it.name }.toTypedArray()

fun Array<String>.toStatusList(): List<ChallengeStatus> = this.map { ChallengeStatus.valueOf(it) }

fun List<ChallengeStatus>.containsActiveChallenge(): Boolean =
    this.contains(ChallengeStatus.SCHEDULED) || this.contains(ChallengeStatus.PENDING)