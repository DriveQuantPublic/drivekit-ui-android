package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.graphics.Typeface
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal

class GaugeIndicator(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val textView: TextView
    private val gaugeView: GaugeView
    private val imageView: ImageView

    init {
        inflate(context, R.layout.layout_score_gauge, this)

        textView = findViewById(R.id.score)
        gaugeView = findViewById(R.id.gauge)
        imageView = findViewById(R.id.image)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.GaugeIndicator)

        imageView.setImageDrawable(attributes.getDrawable(R.styleable.GaugeIndicator_image))

        gaugeView.updateStrokeSize(
            attributes.getDimension(
                R.styleable.GaugeIndicator_strokeSize,
                resources.getDimension(R.dimen.dk_default_gauge_stroke)
            )
        )

        gaugeView.setBackGaugeColor(
            attributes.getColor(
                R.styleable.GaugeIndicator_backColor,
                DriveKitUI.colors.neutralColor()
            )
        )

        textView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            attributes.getDimensionPixelSize(
                R.styleable.GaugeIndicator_textSize,
                resources.getDimensionPixelSize(R.dimen.dk_text_normal)
            ).toFloat()
        )
        textView.setTextColor(DriveKitUI.colors.complementaryFontColor())
        attributes.recycle()
    }

    fun configure(score: Double, type: GaugeType, scoreStyle: Int = Typeface.NORMAL) {
        gaugeView.setOpenAngle(128F)
        gaugeView.setStartAngle(38F)
        textView.text = score.removeZeroDecimal()
        textView.setTypeface(DriveKitUI.secondaryFont(context), scoreStyle)
        gaugeView.configureScore(score)
        gaugeView.setGaugeColor(ContextCompat.getColor(context, type.getColor(score)))
        imageView.setImageDrawable(ContextCompat.getDrawable(context, type.getDrawable()))
    }
}

enum class GaugeType {
    SAFETY, ECO_DRIVING, DISTRACTION;

    fun getColor(score: Double): Int {
        return getColorFromValue(score, getSteps())
    }

    fun getDrawable(): Int {
        return when (this) {
            ECO_DRIVING -> R.drawable.dk_common_ecodriving
            SAFETY -> R.drawable.dk_common_safety
            DISTRACTION -> R.drawable.dk_common_distraction
        }
    }

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

    private fun getSteps(): List<Double> {
        return when (this) {
            ECO_DRIVING -> {
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
            SAFETY -> listOf(0.0, 5.5, 6.5, 7.5, 8.5, 9.5, 10.0)
            DISTRACTION -> listOf(1.0, 7.0, 8.0, 8.5, 9.0, 9.5, 10.0)
        }
    }
}