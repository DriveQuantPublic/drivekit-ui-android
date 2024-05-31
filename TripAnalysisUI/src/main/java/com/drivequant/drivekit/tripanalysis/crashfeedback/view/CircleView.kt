package com.drivequant.drivekit.tripanalysis.crashfeedback.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.extension.intColor

internal class CircleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val drawPaint: Paint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(
            (width/2).toFloat(),
            (height/2).toFloat(),
            (width/2.5).toFloat(),
            drawPaint
        )
    }

    init {
        drawPaint.color = R.color.dkCrashFeedbackAssistance_10.intColor(context)
        drawPaint.isAntiAlias = true
    }
}
