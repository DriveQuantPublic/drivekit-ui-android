package com.drivequant.drivekit.ui.extension

import com.drivequant.drivekit.common.ui.component.GaugeType

fun GaugeType.getScoreInfoTitle(): String =
    when (this) {
        GaugeType.SAFETY -> "dk_driverdata_safety_score"
        GaugeType.ECO_DRIVING -> "dk_driverdata_eco_score"
        GaugeType.DISTRACTION -> "dk_driverdata_distraction_score"
        GaugeType.SPEEDING -> "dk_driverdata_speeding_score"
    }


fun GaugeType.getScoreInfoContent(): String =
    when (this) {
        GaugeType.SAFETY -> "dk_driverdata_safety_score_info"
        GaugeType.ECO_DRIVING -> "dk_driverdata_eco_score_info"
        GaugeType.DISTRACTION -> "dk_driverdata_distraction_score_info"
        GaugeType.SPEEDING -> "dk_driverdata_speeding_score_info"
    }