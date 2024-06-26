package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.intColor
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKSpannable

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
                DKColors.neutralColor
            )
        )

        textView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            attributes.getDimensionPixelSize(
                R.styleable.GaugeIndicator_textSize,
                resources.getDimensionPixelSize(R.dimen.dk_text_normal)
            ).toFloat()
        )
        attributes.recycle()
    }

    @JvmOverloads
    fun configure(
        value: Double,
        type: DKGaugeConfiguration,
        scoreStyle: Int = Typeface.NORMAL,
        title: Spannable = DKSpannable().append(value.removeZeroDecimal()).toSpannable()
    ) {
        gaugeView.setOpenAngle(type.getGaugeType().getOpenAngle())
        gaugeView.setStartAngle(type.getGaugeType().getStartAngle())
        textView.text = title
        textView.setTypeface(DriveKitUI.secondaryFont(context), scoreStyle)
        gaugeView.configureMaxScore(type.getMaxScore())
        gaugeView.configureScore(value)
        gaugeView.setGaugeColor(type.getColor(value).intColor(context))
        type.getIcon()?.let {
            imageView.setImageDrawable(ContextCompat.getDrawable(context, it))
        }
    }
}
