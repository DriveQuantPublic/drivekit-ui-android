package com.drivequant.drivekit.driverachievement.ui.commons.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.SeekBar

import com.drivequant.drivekit.driverachievement.ui.R

class CustomSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.seekBarStyle
) : SeekBar(context, attrs, defStyleAttr) {

    private val mThumbSize: Int = resources.getDimensionPixelSize(R.dimen.thumb_size)
    private val mTextPaint: TextPaint = TextPaint()

    init {
        mTextPaint.color = Color.WHITE
        mTextPaint.textSize = resources.getDimensionPixelSize(R.dimen.thumb_text_size).toFloat()
        mTextPaint.typeface = Typeface.DEFAULT_BOLD
        mTextPaint.textAlign = Paint.Align.CENTER
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val progressText = progress.toString()
        val bounds = Rect()
        mTextPaint.getTextBounds(progressText, 0, progressText.length, bounds)

        val leftPadding = paddingLeft - thumbOffset
        val rightPadding = paddingRight - thumbOffset
        val width = width - leftPadding - rightPadding
        val progressRatio = progress.toFloat() / max
        val thumbOffset = mThumbSize * (.5f - progressRatio)
        val thumbX = progressRatio * width + leftPadding.toFloat() + thumbOffset
        val thumbY = height / 2f + bounds.height() / 2f
        canvas.drawText(progressText, thumbX, thumbY, mTextPaint)
    }
}