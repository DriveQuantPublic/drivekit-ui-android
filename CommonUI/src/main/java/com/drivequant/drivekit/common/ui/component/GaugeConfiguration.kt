package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import java.math.BigDecimal
import java.math.RoundingMode

sealed class GaugeConfiguration(open val value: Double) : DKGaugeConfiguration {
    data class SAFETY(override val value: Double) : GaugeConfiguration(value)
    data class ECO_DRIVING(override val value: Double) : GaugeConfiguration(value)
    data class DISTRACTION(override val value: Double) : GaugeConfiguration(value)
    data class SPEEDING(override val value: Double) : GaugeConfiguration(value)

    override fun getTitle(context: Context): Spannable {
        val text = if (value == 11.0) {
            "-"
        } else {
            BigDecimal(value).setScale(1, RoundingMode.HALF_UP).toDouble().removeZeroDecimal()
        }
        return DKSpannable().append(text, context.resSpans {
            size(R.dimen.dk_text_xxbig)
            typeface(Typeface.BOLD)
            color(DriveKitUI.colors.mainFontColor())
        }).toSpannable()
    }

    override fun getScore() = value

    override fun getMaxScore(): Double = 10.0

    override fun getColor(value: Double): Int = getColorFromValue(value, getSteps())

    override fun getIcon(): Int = when (this) {
        is ECO_DRIVING -> R.drawable.dk_common_ecodriving
        is SAFETY -> R.drawable.dk_common_safety
        is DISTRACTION -> R.drawable.dk_common_distraction
        is SPEEDING -> R.drawable.dk_common_eco_accel
    }

    override fun getGaugeType(): DKGaugeType = DKGaugeType.OPEN_WITH_IMAGE(getIcon())

    private fun getColorFromValue(value: Double, steps: List<Double>): Int {
        if (value == 11.0)
            return R.color.dkGaugeBackColor
        if (value <= steps[1])
            return R.color.dkVeryBad
        if (value <= steps[2])
            return R.color.dkBad
        if (value <= steps[3])
            return R.color.dkBadMean
        if (value <= steps[4])
            return R.color.dkMean
        if (value <= steps[5])
            return R.color.dkGoodMean
        return if (value <= steps[6]) R.color.dkGood else R.color.dkExcellent
    }

    private fun getSteps() = when (this) {
        is SAFETY -> com.drivequant.drivekit.core.scoreslevels.DKScoreType.SAFETY
        is ECO_DRIVING -> com.drivequant.drivekit.core.scoreslevels.DKScoreType.ECO_DRIVING
        is DISTRACTION -> com.drivequant.drivekit.core.scoreslevels.DKScoreType.DISTRACTION
        is SPEEDING -> com.drivequant.drivekit.core.scoreslevels.DKScoreType.SPEEDING
    }.getSteps()
}