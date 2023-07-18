package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout

open class DKRoundedCornerFrameLayout : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val path = Path()
    private val rect = RectF(0f, 0f, 0f, 0f)
    private var corners: FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    fun roundCorners(
        topLeftRadius: Float,
        topRightRadius: Float,
        bottomRightRadius: Float,
        bottomLeftRadius: Float
    ) {
        this.corners = floatArrayOf(
            topLeftRadius,
            topLeftRadius,
            topRightRadius,
            topRightRadius,
            bottomRightRadius,
            bottomRightRadius,
            bottomLeftRadius,
            bottomLeftRadius
        )
        invalidate()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        this.rect.set(0f, 0f, width.toFloat(), height.toFloat())
        this.path.reset()
        this.path.addRoundRect(this.rect, this.corners, Path.Direction.CW)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.clipPath(this.path)
        super.dispatchDraw(canvas)
    }
}
