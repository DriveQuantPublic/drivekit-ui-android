package com.drivequant.drivekit.challenge.ui.challengelist.viewmodel

import androidx.annotation.DrawableRes
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.databaseutils.entity.ChallengeGroup
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus
import java.util.Date

class ChallengeData(
    val challengeId: String,
    val title: String,
    val description: String,
    val conditionsDescription: String?,
    val startDate: Date,
    val endDate: Date,
    val rank: Int,
    val rankKey: String?,
    val themeCode: Int,
    val iconCode: Int,
    val type: Int,
    val isRegistered: Boolean,
    val conditionsFilled: Boolean,
    val driverConditions: Map<String, String>,
    val groups: List<ChallengeGroup>,
    val rules: String?,
    val status: ChallengeStatus
) {

    @DrawableRes
    fun getChallengeResourceId(): Int = when (iconCode) {
            101 -> R.drawable.dk_challenge_general_101_trophy
            102 -> R.drawable.dk_challenge_general_102_medal
            103 -> R.drawable.dk_challenge_general_103_medal_first
            104 -> R.drawable.dk_challenge_general_104_leader_board
            105 -> R.drawable.dk_challenge_general_105_steering_wheel
            301 -> R.drawable.dk_challenge_eco_drive_301_leaf
            302 -> R.drawable.dk_challenge_eco_drive_302_natural
            303 -> R.drawable.dk_challenge_eco_drive_303_gas_pump
            304 -> R.drawable.dk_challenge_eco_drive_304_gas_station
            401 -> R.drawable.dk_challenge_safety_401_shield
            402 -> R.drawable.dk_challenge_safety_402_tire
            403 -> R.drawable.dk_challenge_safety_403_wheel
            404 -> R.drawable.dk_challenge_safety_404_brake_warning
            405 -> R.drawable.dk_challenge_safety_405_speedometer01
            406 -> R.drawable.dk_challenge_safety_406_speedometer02
            407 -> R.drawable.dk_challenge_safety_407_maximum_speed
            408 -> R.drawable.dk_challenge_safety_408_traffic_light
            else -> R.drawable.dk_challenge_general_101_trophy
        }

    fun shouldDisplayChallengeDetail() = (status == ChallengeStatus.FINISHED && isRegistered) || (status == ChallengeStatus.PENDING && isRegistered && conditionsFilled)

    fun shouldDisplayExplaining() = status == ChallengeStatus.FINISHED && (!isRegistered || !conditionsFilled)
}

interface ChallengeListener {
    fun onClickChallenge(challengeData: ChallengeData)
}
