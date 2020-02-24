package com.drivequant.drivekit.common.ui.utils

import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan

object DKSpannable {

    enum class SizeSpan {
        MEDIUM, LARGE, SMALL
    }

    private lateinit var builder: SpannableStringBuilder
    private var elements: MutableList<String> = mutableListOf()
    private var styles: MutableList<Int> = mutableListOf()
    private var colors: MutableList<Int> = mutableListOf()
    private var sizes: MutableList<RelativeSizeSpan> = mutableListOf()

    private var textWithValues:MutableMap<String,MutableList<Any>?> = mutableMapOf()

    fun init(text: String) = apply {
        builder = SpannableStringBuilder()
        textWithValues[text] = null
    }

    fun append(text: String): DKSpannable = apply {
        textWithValues[text] = null
    }

    fun setSize(sizeSpan: SizeSpan): DKSpannable = apply {
        val size = when (sizeSpan) {
            SizeSpan.SMALL -> RelativeSizeSpan(5.0f)
            SizeSpan.MEDIUM -> RelativeSizeSpan(2.0f)
            SizeSpan.LARGE -> RelativeSizeSpan(5.0f)
        }
        //textWithValues.get()
    }

    fun setStyle(style: Int): DKSpannable = apply {
        styles.add(style)
    }

    fun setColor(color: Int): DKSpannable = apply {
        colors.add(color)
    }

    fun build(): SpannableStringBuilder {

        for (text in elements) {
            for (style in styles) {
                builder.setSpan(StyleSpan(style), 0, text.length, SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            for (size in sizes) {
                builder.setSpan(size, 0, text.length, SPAN_EXCLUSIVE_EXCLUSIVE)

            }

            for (color in colors) {
                builder.setSpan(
                    ForegroundColorSpan(color),
                    0,
                    text.length,
                    SPAN_EXCLUSIVE_EXCLUSIVE
                )

            }
        }

        return builder
    }
}
