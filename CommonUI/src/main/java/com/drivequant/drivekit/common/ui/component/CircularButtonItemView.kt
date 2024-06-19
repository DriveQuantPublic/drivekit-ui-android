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
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.extension.withAlpha
import com.drivequant.drivekit.common.ui.graphical.DKColors

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
        val tintColor = if (itemSelected) DKColors.mainFontColor else DKColors.complementaryFontColor
        val circle = ContextCompat.getDrawable(context, R.drawable.dk_circle_accent)
        val bgColor = if (selected) {
            R.color.secondaryColor.withAlpha(context, 102)
        } else {
            DKColors.transparentColor
        }
        (circle as? GradientDrawable)?.setColor(bgColor)
        imageView.drawable.tintDrawable(tintColor)
        circleView.background = circle
    }

    private fun setItemDrawable(resDrawableId: Int) {
        if (resDrawableId != 0) {
            val drawable: Drawable? = ContextCompat.getDrawable(context, resDrawableId)
            if (drawable != null) {
                drawable.tintDrawable(if (itemSelected) DKColors.secondaryColor else DKColors.complementaryFontColor)
                imageView.setImageDrawable(drawable)
            }
        }
    }

    private fun setItemSelected(itemSelected: Boolean) {
        this.itemSelected = itemSelected
    }
}
