package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.intColor
import com.drivequant.drivekit.common.ui.extension.smallTextWithColor
import com.drivequant.drivekit.common.ui.extension.tint

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
        defStyleAttr) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        val view = inflate(context, R.layout.dk_layout_edit_drawable_text, null)
        imageView = view.findViewById(R.id.image_view)
        textView = view.findViewById(R.id.edit_text)
        textView?.smallTextWithColor(R.color.dkGrayColor.intColor(context))
        if (attrs != null) {
            val themeTypedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.DKEditableDrawableText,
                0,
                0
            )
            for (i in 0 until themeTypedArray.indexCount) {
                when (val attr = themeTypedArray.getIndex(i)) {
                    R.styleable.DKEditableDrawableText_editTextTitle -> setEditTextTitle(themeTypedArray.getString(attr))
                    R.styleable.DKEditableDrawableText_editTextTitleHint -> setEditTextHint(themeTypedArray.getString(attr))
                    R.styleable.DKEditableDrawableText_editTextTitleDrawable -> setEditTextDrawable(themeTypedArray.getResourceId(attr, -1))
                }
            }
            themeTypedArray.recycle()
            val inputTypeTypedArray =
                context.obtainStyledAttributes(
                    attrs,
                    intArrayOf(android.R.attr.inputType),
                    0,
                    0)
            setInputType(inputTypeTypedArray.getInt(0, inputType))
            inputTypeTypedArray.recycle()
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

    fun setEditTextTitle(
        title: String?,
        color: Int = R.color.dkGrayColor.intColor(context)
    ) {
        if (!title.isNullOrBlank()) {
            textView?.text = title
        }
        textView?.smallTextWithColor(color)
    }

    private fun setEditTextHint(
        hint: String?,
        color: Int = R.color.dkGrayColor.intColor(context)
    ) {
        if (!hint.isNullOrBlank()) {
            textView?.hint = hint
        }
        textView?.setTextColor(color)
    }

    private fun setEditTextDrawable(@DrawableRes drawableResId: Int) {
        if (drawableResId != -1) {
            imageView?.let { imageView ->
                ContextCompat.getDrawable(context, drawableResId)?.let { drawable ->
                    drawable.tint(context, R.color.mainFontColor)
                    imageView.setImageDrawable(drawable)
                }
            }
        }
    }
}
