package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.tintFromHueOfColor

internal class CustomRoadContextBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rectF = RectF(0f, 0f, 0f, 0f)
    private val rectPath = Path()

    private val cornerRadius = 30f
    private var measuredWidth = 0f
    private var measuredHeight = 0f

    private var progressItems = listOf<ProgressItem>()

    fun init(progressItems: List<ProgressItem>) {
        this.progressItems = progressItems
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.clipPath(rectPath)

        var lastProgress = 0

        for (progressItem in progressItems) {
            val progressPaint = createPaint(progressItem)
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
            canvas?.drawRect(rectF, progressPaint)
            lastProgress = progressItemStart
        }
    }

    private fun computeProgressWidth(progressItem: ProgressItem) =
        (progressItem.progressItemPercentage * width / 100).toInt()

    private fun createPaint(progressItem: ProgressItem): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = ContextCompat.getColor(
            context,
            progressItem.color
        ).tintFromHueOfColor(DriveKitUI.colors.primaryColor())
        return paint
    }
}

internal data class ProgressItem(
    @ColorRes val color: Int,
    val progressItemPercentage: Double
)
