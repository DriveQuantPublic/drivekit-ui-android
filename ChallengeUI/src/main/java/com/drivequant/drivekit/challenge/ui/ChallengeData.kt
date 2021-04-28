package com.drivequant.drivekit.challenge.ui

import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus
import com.drivequant.drivekit.databaseutils.entity.Group

class ChallengeData(
    val challengeId: String,
    val title: String,
    val description: String,
    val conditionsDescription: String?,
    val startDate: String,
    val endDate: String,
    val conditions: Map<String, String>,
    val rankKey: String?,
    val themeCode: Int,
    val iconCode: Int,
    val type: Int,
    val isRegistered: Boolean,
    val conditionsFilled: Boolean,
    val driverConditions: Map<String, String>,
    val groups: List<Group>,
    val rules: String?,
    val optinText: String?,
    val status: ChallengeStatus
)