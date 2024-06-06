package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextUtils.concat
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.graphical.DKStyle

class DKSpannable {

    private var length = 0
    private var elements = ArrayList<CharSequence>()
    private val values: MutableMap<IntRange, Iterable<Any>> = HashMap()

    fun append(text: CharSequence, spans: Iterable<Any>) = apply {
        val end = text.length
        elements.add(text)
        values[(length..length + end)] = spans
        length += end
    }

    fun append(context: Context, text: CharSequence, @ColorInt color: Int, style: DKStyle) = apply {
        append(text, context.resSpans {
            color(color)
            style(style)
        })
    }

    fun append(newText: CharSequence) = apply {
        elements.add(newText)
        length += newText.length
    }

    fun appendSpace(newText: CharSequence, before: Boolean = true) =
        if (before) append(" ").append(newText) else append(newText).append(" ")

    fun space() = append(" ")

    fun toSpannable() = SpannableString(concat(*elements.toTypedArray())).apply {
        values.forEach {
            val range = it.key
            it.value.forEach { value ->
                setSpan(value, range.first, range.last, SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
}

class ResSpans(private val context: Context) : Iterable<Any> {
    private val spans = ArrayList<Any>()

    override fun iterator() = spans.iterator()

    fun appearance(@StyleRes id: Int) = spans.add(TextAppearanceSpan(context, id))

    fun size(@DimenRes id: Int) =
        spans.add(AbsoluteSizeSpan(context.resources.getDimension(id).toInt()))

    fun style(style: DKStyle) {
        size(style.dimensionId())
        typeface(style.typefaceStyle())
    }

    fun color(@ColorInt color: Int) = spans.add(ForegroundColorSpan(color))

    fun icon(@DrawableRes id: Int, size: Int) =
        spans.add(ImageSpan(AppCompatResources.getDrawable(context, id)!!.apply {
            setBounds(0, 0, size, size)
        }))

    fun typeface(family: String) = spans.add(TypefaceSpan(family))

    fun typeface(style: Int) = spans.add(StyleSpan(style))

    fun typeface(typefaceSpan: CustomTypefaceSpan) = spans.add(typefaceSpan)

    fun click(action: () -> Unit) = spans.add(clickableSpan(action))

    fun custom(span: Any) = spans.add(span)
}

fun clickableSpan(action: () -> Unit) = object : ClickableSpan() {
    override fun onClick(view: View) = action()
}
