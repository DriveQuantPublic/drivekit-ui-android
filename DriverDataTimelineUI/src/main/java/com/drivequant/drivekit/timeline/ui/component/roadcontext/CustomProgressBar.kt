package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatSeekBar(context, attrs) {

    private var progressItems = mutableListOf<ProgressItem>()

    fun initData(progressItems: MutableList<ProgressItem>) {
        this.progressItems = progressItems
    }

    @Synchronized
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        if (progressItems.isNotEmpty()) {
            val progressBarHeight = height
            var lastProgressX = 0
            for (progressItem in progressItems) {
                val progressPaint = Paint()
                progressPaint.color = ContextCompat.getColor(
                    context,
                    progressItem.color
                )
                val progressItemWidth = (progressItem.progressItemPercentage * width / 100).toInt()
                var progressItemRight = lastProgressX + progressItemWidth

                if (progressItems.indexOf(progressItem) == progressItems.size - 1
                    && progressItemRight != width
                ) {
                    progressItemRight = width
                }
                val progressRect = RectF()
                progressRect.set(
                    lastProgressX.toFloat(), thumbOffset.toFloat() / 2,
                    progressItemRight.toFloat(), progressBarHeight.toFloat() - thumbOffset / 2
                )

                canvas.drawRoundRect(progressRect, 0f, 0f, progressPaint)
                lastProgressX = progressItemRight
            }
            super.onDraw(canvas)
        }
    }
}