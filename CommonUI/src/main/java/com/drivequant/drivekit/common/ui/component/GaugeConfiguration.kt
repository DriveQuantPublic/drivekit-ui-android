package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
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
            color(DKColors.mainFontColor)
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
        return if (value == 11.0) {
            R.color.dkGaugeBackColor
        } else if (value <= steps[1]) {
            R.color.dkVeryBad
        } else if (value <= steps[2]) {
            R.color.dkBad
        } else if (value <= steps[3]) {
            R.color.dkBadMean
        } else if (value <= steps[4]) {
            R.color.dkMean
        } else if (value <= steps[5]) {
            R.color.dkGoodMean
        } else if (value <= steps[6]) {
            R.color.dkGood
        } else {
            R.color.dkExcellent
        }
    }

    private fun getSteps() = when (this) {
        is SAFETY -> DKScoreType.SAFETY
        is ECO_DRIVING -> DKScoreType.ECO_DRIVING
        is DISTRACTION -> DKScoreType.DISTRACTION
        is SPEEDING -> DKScoreType.SPEEDING
    }.getSteps()
}
