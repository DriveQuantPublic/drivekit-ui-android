package com.drivequant.drivekit.ui.extension

import android.content.Context
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.utils.DKResource

fun GaugeConfiguration.getScoreInfoTitle(context: Context): String {
    val titleIdentifier = when (this) {
        GaugeConfiguration.SAFETY -> "dk_driverdata_safety_score"
        GaugeConfiguration.ECO_DRIVING -> "dk_driverdata_eco_score"
        GaugeConfiguration.DISTRACTION -> "dk_driverdata_distraction_score"
        GaugeConfiguration.SPEEDING -> "dk_driverdata_speeding_score"
    }
    return DKResource.convertToString(context, titleIdentifier)
}


fun GaugeConfiguration.getScoreInfoContent(context: Context): String {
    val contentIdentifier = when (this) {
        GaugeConfiguration.SAFETY -> "dk_driverdata_safety_score_info"
        GaugeConfiguration.ECO_DRIVING -> "dk_driverdata_eco_score_info"
        GaugeConfiguration.DISTRACTION -> "dk_driverdata_distraction_score_info"
        GaugeConfiguration.SPEEDING -> "dk_driverdata_speeding_score_info"
    }
    return DKResource.convertToString(context, contentIdentifier)
}