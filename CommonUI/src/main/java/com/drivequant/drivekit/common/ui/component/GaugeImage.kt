package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.intColor
import com.drivequant.drivekit.common.ui.graphical.DKColors

class GaugeImage(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val gaugeView: GaugeView
    private val imageView: ImageView

    init {
        inflate(context, R.layout.layout_image_gauge, this)

        gaugeView = findViewById(R.id.gauge)
        imageView = findViewById(R.id.image_view_gauge)

        val gaugeImagePadding =
            context.resources.getDimension(
                R.dimen.dk_default_gauge_image_stroke
            ).toInt() + context.resources.getDimension(R.dimen.dk_margin_quarter).toInt()

        imageView.setPadding(
            gaugeImagePadding,
            gaugeImagePadding,
            gaugeImagePadding,
            gaugeImagePadding
        )
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.GaugeImage)

        imageView.setImageDrawable(attributes.getDrawable(R.styleable.GaugeImage_gaugeImage))

        gaugeView.updateStrokeSize(
            attributes.getDimension(
                R.styleable.GaugeImage_gaugeStrokeSize,
                resources.getDimension(R.dimen.dk_default_gauge_image_stroke)
            )
        )

        gaugeView.setBackGaugeColor(
            attributes.getColor(
                R.styleable.GaugeImage_gaugeBackColor,
                DKColors.transparentColor
            )
        )
        attributes.recycle()
    }

    fun configure(progress: Double, gaugeDrawable: Drawable, @ColorRes gaugeColor: Int) {
        gaugeView.setOpenAngle(0F)
        gaugeView.setStartAngle(270F)
        gaugeView.configureScore(progress)
        imageView.setImageDrawable(gaugeDrawable)
        try {
            gaugeView.setGaugeColor(gaugeColor.intColor(context))
        } catch (e: Resources.NotFoundException) {
            gaugeView.setGaugeColor(gaugeColor)
        }
    }
}
