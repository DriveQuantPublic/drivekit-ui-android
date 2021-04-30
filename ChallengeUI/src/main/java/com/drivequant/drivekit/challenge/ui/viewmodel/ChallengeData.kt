package com.drivequant.drivekit.challenge.ui.viewmodel

import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus
import com.drivequant.drivekit.databaseutils.entity.Group

class ChallengeData(
    val challengeId: String,
    val title: String,
    val description: String,
    val conditionsDescription: String?,
    val startDate: String,
    val endDate: String,
    val rankKey: String?,
    val themeCode: Int,
    val iconCode: Int,
    val type: Int,
    val isRegistered: Boolean,
    val conditionsFilled: Boolean,
    val driverConditions: Map<String, String>,
    val groups: List<Group>,
    val rules: String?,
    val status: ChallengeStatus
) {

    fun getChallengeResourceId(): String = when (iconCode) {
            101 -> "dk_challenge_general_101_trophy"
            102 -> "dk_challenge_general_102_metal"
            103 -> "dk_challenge_general_103_medal_first"
            104 -> "dk_challenge_general_104_leader_board"
            105 -> "dk_challenge_general_105_steering_wheel"
            201 -> "dk_challenge_vehicle_201_sedan"
            202 -> "dk_challenge_vehicle_202_multiple_cars"
            203 -> "dk_challenge_vehicle_203_motorbike"
            204 -> "dk_challenge_vehicle_204_scooter"
            205 -> "dk_challenge_vehicle_205_helmet"
            206 -> "dk_challenge_vehicle_206_car"
            207 -> "dk_challenge_vehicle_207_car2"
            208 -> "dk_challenge_vehicle_208_convertible"
            209 -> "dk_challenge_vehicle_209_bus"
            210 -> "dk_challenge_vehicle_210_truck"
            211 -> "dk_challenge_vehicle_211_semi_truck"
            301 -> "dk_challenge_eco_drive_301_leaf"
            302 -> "dk_challenge_eco_drive_302_natural"
            303 -> "dk_challenge_eco_drive_303_gas_pump"
            304 -> "dk_challenge_eco_drive_304_gas_station"
            401 -> "dk_challenge_safety_401_shield"
            402 -> "dk_challenge_safety_402_tire"
            403 -> "dk_challenge_safety_403_wheel"
            404 -> "dk_challenge_safety_404_brake_warning"
            405 -> "dk_challenge_safety_405_speedometer01"
            406 -> "dk_challenge_safety_406_speedometer02"
            407 -> "dk_challenge_safety_407_maximum_speed"
            408 -> "dk_challenge_safety_408_traffic_light"
            501 -> "dk_challenge_seasons_501_summer"
            502 -> "dk_challenge_seasons_502_sun_glasses"
            503 -> "dk_challenge_seasons_503_spring"
            504 -> "dk_challenge_seasons_504_christmas"
            505 -> "dk_challenge_seasons_505_winter"
            601 -> "dk_challenge_gift_601_gift"
            602 -> "dk_challenge_gift_602_money_bag_euro"
            603 -> "dk_challenge_gift_603_money_bag_dollar"
            604 -> "dk_challenge_gift_604_paper_money"
            else -> "dk_challenge_general_101_trophy"
        }

}

