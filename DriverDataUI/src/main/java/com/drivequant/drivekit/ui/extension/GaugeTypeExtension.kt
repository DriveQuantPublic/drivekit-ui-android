package com.drivequant.drivekit.ui.extension

import android.content.Context
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.ui.R

fun GaugeConfiguration.getScoreInfoTitle(context: Context): String =
    when (this) {
        is GaugeConfiguration.SAFETY -> R.string.dk_driverdata_safety_score
        is GaugeConfiguration.ECO_DRIVING -> R.string.dk_driverdata_ecodriving_score
        is GaugeConfiguration.DISTRACTION -> R.string.dk_driverdata_distraction_score
        is GaugeConfiguration.SPEEDING -> R.string.dk_driverdata_speeding_score
    }.let {
        context.getString(it)
    }


fun GaugeConfiguration.getScoreInfoContent(context: Context): String =
    when (this) {
        is GaugeConfiguration.SAFETY -> R.string.dk_driverdata_safety_score_info
        is GaugeConfiguration.ECO_DRIVING -> R.string.dk_driverdata_ecodriving_score_info
        is GaugeConfiguration.DISTRACTION -> R.string.dk_driverdata_distraction_score_info
        is GaugeConfiguration.SPEEDING -> R.string.dk_driverdata_speeding_score_info
    }.let {
        context.getString(it)
    }
