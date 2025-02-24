package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import com.drivequant.drivekit.common.ui.DriveKitUI

open class DKButtonBase : androidx.appcompat.widget.AppCompatButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val path = Path()
    private val rect = RectF(0f, 0f, 0f, 0f)

    val shape = GradientDrawable()

    init {
        this.background = shape
    }

    fun updateTypeface() {
        setTypeface(DriveKitUI.primaryFont(context), typeface.style)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val radius = height / 2f
        val corners = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)

        this.rect.set(0f, 0f, width.toFloat(), height.toFloat())

        this.path.reset()
        this.path.addRoundRect(this.rect, corners, Path.Direction.CW)

        this.shape.cornerRadii = corners
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.clipPath(this.path)
        super.dispatchDraw(canvas)
    }
}
