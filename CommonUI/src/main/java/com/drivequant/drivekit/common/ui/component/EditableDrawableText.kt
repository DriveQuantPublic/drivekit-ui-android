package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.tintDrawable

class EditableDrawableText : LinearLayout {

    private var inputType = InputType.TYPE_CLASS_TEXT
    private var imageView: ImageView? = null
    private var textView: TextView? = null

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        val view = inflate(context, R.layout.dk_layout_edit_drawable_text, null)
        imageView = view.findViewById(R.id.image_view)
        textView = view.findViewById(R.id.edit_text)
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.DKEditableDrawableText,
                0,
                0
            )
            val n = a.indexCount
            for (i in 0 until n) {
                val attr = a.getIndex(i)
                if (attr == R.styleable.DKEditableDrawableText_editTextTitle) {
                    setEditTextTitle(a.getString(attr))
                } else if (attr == R.styleable.DKEditableDrawableText_editTextTitleHint) {
                    setEditTextHint(a.getString(attr))
                } else if (attr == R.styleable.DKEditableDrawableText_editTextTitleDrawable) {
                    setEditTextDrawable(a.getResourceId(attr, -1))
                }
            }
            a.recycle()
            val b =
                context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.inputType), 0, 0)
            setInputType(b.getInt(0, inputType))
            b.recycle()
        }
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun setInputType(inputType: Int) {
        this.inputType = inputType
        textView?.inputType = inputType
    }

    fun setEditTextTitle(title: String?) {
        if (!TextUtils.isEmpty(title)) {
            textView!!.text = title
        }
    }

    private fun setEditTextHint(hint: String?) {
        if (!hint.isNullOrBlank()) {
            textView?.hint = hint
        }
    }

    private fun setEditTextDrawable(drawableResId: Int) {
        if (drawableResId > 0) {
            ContextCompat.getDrawable(context, drawableResId)?.let {
                DrawableCompat.wrap(it).mutate().tintDrawable(DriveKitUI.colors.mainFontColor())
                imageView!!.setImageDrawable(it)
            }
        }
    }
}