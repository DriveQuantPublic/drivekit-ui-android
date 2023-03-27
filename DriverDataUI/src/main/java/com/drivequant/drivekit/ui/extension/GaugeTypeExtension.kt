package com.drivequant.drivekit.ui.extension

import android.content.Context
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.utils.DKResource

fun GaugeConfiguration.getScoreInfoTitle(context: Context): String =
    when (this) {
        is GaugeConfiguration.SAFETY -> "dk_driverdata_safety_score"
        is GaugeConfiguration.ECO_DRIVING -> "dk_driverdata_ecodriving_score"
        is GaugeConfiguration.DISTRACTION -> "dk_driverdata_distraction_score"
        is GaugeConfiguration.SPEEDING -> "dk_driverdata_speeding_score"
    }.let {
        DKResource.convertToString(context, it)
    }


fun GaugeConfiguration.getScoreInfoContent(context: Context): String =
    when (this) {
        is GaugeConfiguration.SAFETY -> "dk_driverdata_safety_score_info"
        is GaugeConfiguration.ECO_DRIVING -> "dk_driverdata_ecodriving_score_info"
        is GaugeConfiguration.DISTRACTION -> "dk_driverdata_distraction_score_info"
        is GaugeConfiguration.SPEEDING -> "dk_driverdata_speeding_score_info"
    }.let {
        DKResource.convertToString(context, it)
    }