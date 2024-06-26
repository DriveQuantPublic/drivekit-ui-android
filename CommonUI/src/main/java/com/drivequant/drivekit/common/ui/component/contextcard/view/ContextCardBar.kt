package com.drivequant.drivekit.common.ui.component.contextcard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.common.ui.extension.tintFromHueOfColor

internal class ContextCardBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rectF = RectF(0f, 0f, 0f, 0f)
    private val rectPath = Path()

    private val cornerRadius = 30f
    private var measuredWidth = 0f
    private var measuredHeight = 0f

    private var progressItems = listOf<DKContextCardItem>()

    private var paints = mutableMapOf<DKContextCardItem, Paint>()

    fun update(progressItems: List<DKContextCardItem>) {
        this.progressItems = progressItems

        paints.clear()
        progressItems.forEach {
            paints[it] = createPaint(it)
        }
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        measuredWidth = (right - left).toFloat()
        measuredHeight = (bottom - top).toFloat()
        rectF.set(0f, 0f, measuredWidth, measuredHeight)
        rectPath.reset()
        rectPath.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.clipPath(rectPath)

        var lastProgress = 0

        for (progressItem in progressItems) {
            val progressItemWidth = computeProgressWidth(progressItem)
            var progressItemStart = lastProgress + progressItemWidth

            if (progressItems.indexOf(progressItem) == progressItems.size - 1
                && progressItemStart != width) {
                progressItemStart = width
            }
            rectF.set(
                lastProgress.toFloat(),
                0f,
                progressItemStart.toFloat(),
                measuredHeight
            )

            paints[progressItem]?.let { paint ->
                canvas.drawRect(rectF, paint)
            }

            lastProgress = progressItemStart
        }
    }

    private fun computeProgressWidth(progressItem: DKContextCardItem) =
        (progressItem.getPercent() * width / 100).toInt()

    private fun createPaint(progressItem: DKContextCardItem): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = progressItem.getColorResId().tintFromHueOfColor(context, R.color.primaryColor)
        return paint
    }
}
