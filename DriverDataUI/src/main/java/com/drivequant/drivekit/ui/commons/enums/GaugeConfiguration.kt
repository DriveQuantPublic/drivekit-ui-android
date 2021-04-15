package com.drivequant.drivekit.ui.commons.enums

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.DKGaugeConfiguration
import com.drivequant.drivekit.common.ui.component.DKGaugeType
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable

sealed class GaugeConfiguration(open val value: Double) : DKGaugeConfiguration {
    data class SAFETY(override val value: Double) : GaugeConfiguration(value)
    data class ECO_DRIVING(override val value: Double) : GaugeConfiguration(value)
    data class DISTRACTION(override val value: Double) : GaugeConfiguration(value)
    data class SPEEDING(override val value: Double) : GaugeConfiguration(value)

    override fun getTitle(context: Context): Spannable {
        val text = if (value == 11.0) {
            "-"
        } else {
            value.removeZeroDecimal()
        }
        return DKSpannable().append(text, context.resSpans {
            size(R.dimen.dk_ic_xbig)
            typeface(Typeface.BOLD)
            color(DriveKitUI.colors.complementaryFontColor())
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
        if (value <= steps[0])
            return R.color.dkVeryBad
        if (value <= steps[1])
            return R.color.dkBad
        if (value <= steps[2])
            return R.color.dkBadMean
        if (value <= steps[3])
            return R.color.dkMean
        if (value <= steps[4])
            return R.color.dkGoodMean
        return if (value <= steps[5]) R.color.dkGood else R.color.dkExcellent
    }

    private fun getSteps(): List<Double> = when (this) {
        is ECO_DRIVING -> {
            val mean = 7.63
            val sigma = 0.844
            listOf(
                mean - (2 * sigma),
                mean - sigma,
                mean - (0.25 * sigma),
                mean,
                mean + (0.25 * sigma),
                mean + sigma,
                mean + (2 * sigma)
            )
        }
        is SAFETY -> listOf(0.0, 5.5, 6.5, 7.5, 8.5, 9.5, 10.0)
        is DISTRACTION -> listOf(1.0, 7.0, 8.0, 8.5, 9.0, 9.5, 10.0)
        is SPEEDING -> listOf(3.0, 5.0, 7.0, 8.0, 9.0, 9.5, 10.0)
    }
}