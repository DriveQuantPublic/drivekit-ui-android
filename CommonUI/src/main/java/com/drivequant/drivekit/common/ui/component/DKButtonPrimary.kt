package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.view.ContextThemeWrapper
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R

class DKButtonPrimary : androidx.appcompat.widget.AppCompatButton {

    constructor(context: Context) : super(context) // remove that
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val path = Path()
    private val shape = GradientDrawable()
    private val rect = RectF(0f, 0f, 0f, 0f)

    init {
        shape.apply {
            GradientDrawable.RECTANGLE
            setColor(DriveKitUI.colors.secondaryColor())
        }
        background = shape
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val radius = height / 2f
        val corners = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)

        this.rect.set(0f, 0f, width.toFloat(), height.toFloat())
        this.path.reset()
        this.path.addRoundRect(this.rect, corners, Path.Direction.CW)


        shape.cornerRadii = corners
        //shape.setStroke(3, borderColor)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.clipPath(this.path)
        super.dispatchDraw(canvas)
    }
}