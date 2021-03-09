package com.drivequant.drivekit.ui.extension

import android.content.Context
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.utils.DKResource

fun GaugeType.getScoreInfoTitle(context: Context): String {
    val titleIdentifier = when (this) {
        GaugeType.SAFETY -> "dk_driverdata_safety_score"
        GaugeType.ECO_DRIVING -> "dk_driverdata_eco_score"
        GaugeType.DISTRACTION -> "dk_driverdata_distraction_score"
        GaugeType.SPEEDING -> "dk_driverdata_speeding_score"
    }
    return DKResource.convertToString(context, titleIdentifier)
}


fun GaugeType.getScoreInfoContent(context: Context): String {
    val contentIdentifier = when (this) {
        GaugeType.SAFETY -> "dk_driverdata_safety_score_info"
        GaugeType.ECO_DRIVING -> "dk_driverdata_eco_score_info"
        GaugeType.DISTRACTION -> "dk_driverdata_distraction_score_info"
        GaugeType.SPEEDING -> "dk_driverdata_speeding_score_info"
    }
    return DKResource.convertToString(context, contentIdentifier)
}