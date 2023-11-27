package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R

class CircularButtonItemView : FrameLayout {
    private var itemSelected = false
    private lateinit var imageView: ImageView
    private lateinit var circleView: View

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(
        context: Context, attrs: AttributeSet?
    ) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.dk_layout_circular_button_item_view, null)
        imageView = view.findViewById(R.id.image_view)
        circleView = view.findViewById(R.id.circle_view)
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.DKCircularButtonItemView,
                0,
                0
            )
            for (i in 0 until typedArray.indexCount) {
                val attr = typedArray.getIndex(i)
                if (attr == R.styleable.DKCircularButtonItemView_dkCircularButtonItemDrawable) {
                    setItemDrawable(typedArray.getResourceId(attr, -1))
                }
            }
            typedArray.recycle()
            val b = context.obtainStyledAttributes(
                attrs,
                intArrayOf(android.R.attr.inputType),
                0,
                0
            )
            b.recycle()
        }
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun setItemSelectedState(selected: Boolean) {
        setItemSelected(selected)
        val wrapped: Drawable = DrawableCompat.wrap(imageView.drawable)
        wrapped.mutate()
        val tintColor =
            if (itemSelected) DriveKitUI.colors.mainFontColor() else DriveKitUI.colors.complementaryFontColor()
        val circle = ContextCompat.getDrawable(context, R.drawable.dk_circle_accent)
        val bgColor = if (selected) {
            ColorUtils.setAlphaComponent(DriveKitUI.colors.secondaryColor(), 102)
        } else {
            DriveKitUI.colors.transparentColor()
        }
        (circle as GradientDrawable).setColor(bgColor)
        DrawableCompat.setTint(wrapped, tintColor)
        imageView.setImageDrawable(wrapped)
        circleView.background = circle
    }

    private fun setItemDrawable(resDrawableId: Int) {
        if (resDrawableId != 0) {
            val drawable: Drawable? = ContextCompat.getDrawable(context, resDrawableId)
            if (drawable != null) {
                val wrapped: Drawable = DrawableCompat.wrap(drawable)
                wrapped.mutate()
                DrawableCompat.setTint(
                    drawable,
                    if (itemSelected) DriveKitUI.colors.secondaryColor() else DriveKitUI.colors.complementaryFontColor()
                )
                imageView.setImageDrawable(drawable)
            }
        }
    }

    private fun setItemSelected(itemSelected: Boolean) {
        this.itemSelected = itemSelected
    }
}
