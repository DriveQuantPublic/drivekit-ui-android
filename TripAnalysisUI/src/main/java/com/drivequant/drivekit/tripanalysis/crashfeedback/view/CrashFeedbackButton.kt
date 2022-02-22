package com.drivequant.drivekit.tripanalysis.crashfeedback.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.extension.headLine1


// TODO : set internal
class CrashFeedbackButton : FrameLayout {

    private lateinit var container: FrameLayout
    private lateinit var button: LinearLayout
    private lateinit var textView: TextView

    private var borderColor: Int? = null
    private var bgColor: Int? = null
    private var iconResId: Int? = null
    private var titleResId: Int? = null

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
        container = View.inflate(context, R.layout.dk_layout_crash_feedback_button, null) as FrameLayout
        button = container.findViewById(R.id.button)
        textView = container.findViewById(R.id.text_view)

        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.DKCrashFeedbackButtonItemView,
                0,
                0
            )
            for (i in 0 until typedArray.indexCount) {
                val attr = typedArray.getIndex(i)
                val resId = typedArray.getResourceId(attr, -1)
                when (attr) {
                    R.styleable.DKCrashFeedbackButtonItemView_dkCrashFeedbackButtonItemDrawable -> {
                        iconResId = resId
                    }
                    R.styleable.DKCrashFeedbackButtonItemView_dkCrashFeedbackButtonItemTitle -> {
                        titleResId = resId
                    }
                    R.styleable.DKCrashFeedbackButtonItemView_dkCrashFeedbackButtonBorder -> {
                        borderColor = resId
                    }
                    R.styleable.DKCrashFeedbackButtonItemView_dkCrashFeedbackButtonBackground -> {
                        bgColor = resId
                    }
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
        tintBorder()
        setButtonBackground()
        setText()
        setItemDrawable()
        addView(
            container, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun tintBorder() {
        borderColor?.let {
            if (borderColor != null) {
                (container.background as GradientDrawable).setStroke(
                    6,
                    ContextCompat.getColor(context, it)
                )
            }
        }
    }

    private fun setItemDrawable() {
        iconResId?.let {
            if (iconResId != 0) {
                val drawable: Drawable? = ContextCompat.getDrawable(context, it)
                if (drawable != null) {
                    val bitmap = (drawable as BitmapDrawable).bitmap
                    val size = context.resources.getDimension(R.dimen.dk_ic_big).toInt()
                    BitmapDrawable(
                        context.resources,
                        Bitmap.createScaledBitmap(bitmap, size, size, true)
                    ).let { bitmapDrawable ->
                        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            null,
                            null,
                            bitmapDrawable,
                            null
                        )
                    }
                }
            }
        }
    }

    private fun setText() {
        titleResId?.let {
            if (titleResId != 0) {
                textView.setText(it)
                borderColor?.let { borderColor ->
                    textView.headLine1(ContextCompat.getColor(context, borderColor))
                }
            }
        }
    }

    private fun setButtonBackground() {
        bgColor?.let {
            if (bgColor != 0) {
                button.setBackgroundColor(ContextCompat.getColor(context, it))
            }
        }
    }
}