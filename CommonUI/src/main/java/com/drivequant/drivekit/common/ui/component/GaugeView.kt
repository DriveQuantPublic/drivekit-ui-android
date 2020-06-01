package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.RectF
import kotlin.math.min


class GaugeView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var drawingArea: RectF = RectF()
    private var score: Double = 0.0
    private var openAngle: Float = 0F
    private var startAngle: Float = 0F
    private var strokeSize = 0F
    private var gaugeColor = Color.argb(0, 0, 0, 0)
    private var backGaugeColor = Color.argb(0, 0, 0, 0)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        calculateDrawableArea()

        if (openAngle == 128F) {
            canvas?.drawArc(drawingArea, 270F, openAngle, false, createPaint(Color.argb(0, 0, 0, 0)))
        }
        canvas?.drawArc(drawingArea, startAngle, 360F - openAngle, false, createPaint(backGaugeColor))
        canvas?.drawArc(drawingArea, startAngle, computePercent(), false, createPaint(gaugeColor))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateDrawableArea()
    }

    fun configureScore(score: Double) {
        this.score = score
        invalidate()
    }

    fun setOpenAngle(openAngle: Float) {
        this.openAngle = openAngle
        invalidate()
    }

    fun setStartAngle(startAngle: Float) {
        this.startAngle = startAngle
        invalidate()
    }

    fun setGaugeColor(color: Int) {
        this.gaugeColor = color
        invalidate()
    }

    fun setBackGaugeColor(color: Int) {
        this.backGaugeColor = color
        invalidate()
    }

    fun updateStrokeSize(strokeSize: Float) {
        this.strokeSize = strokeSize
        calculateDrawableArea()
    }

    private fun calculateDrawableArea() {
        val drawPadding = strokeSize / 2
        val width = width.toFloat()
        val height = height.toFloat()
        val edge = min (height , width)
        val right = edge - drawPadding
        val bottom = edge - drawPadding
        drawingArea = RectF(drawPadding, drawPadding, right, bottom)
    }

    private fun computePercent(): Float {
        return ((360 - openAngle) * score / 10F).toFloat()
    }

    private fun createPaint(color: Int): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = strokeSize
        return paint
    }
}