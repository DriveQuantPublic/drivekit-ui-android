package com.drivequant.drivekit.ui.extension

import android.content.Context
import com.drivequant.drivekit.ui.commons.enums.GaugeConfiguration
import com.drivequant.drivekit.common.ui.utils.DKResource

fun GaugeConfiguration.getScoreInfoTitle(context: Context): String {
    val titleIdentifier = when (this) {
        is GaugeConfiguration.SAFETY -> "dk_driverdata_safety_score"
        is GaugeConfiguration.ECO_DRIVING -> "dk_driverdata_eco_score"
        is GaugeConfiguration.DISTRACTION -> "dk_driverdata_distraction_score"
        is GaugeConfiguration.SPEEDING -> "dk_driverdata_speeding_score"
    }
    return DKResource.convertToString(context, titleIdentifier)
}


fun GaugeConfiguration.getScoreInfoContent(context: Context): String {
    val contentIdentifier = when (this) {
        is GaugeConfiguration.SAFETY -> "dk_driverdata_safety_score_info"
        is GaugeConfiguration.ECO_DRIVING -> "dk_driverdata_eco_score_info"
        is GaugeConfiguration.DISTRACTION -> "dk_driverdata_distraction_score_info"
        is GaugeConfiguration.SPEEDING -> "dk_driverdata_speeding_score_info"
    }
    return DKResource.convertToString(context, contentIdentifier)
}