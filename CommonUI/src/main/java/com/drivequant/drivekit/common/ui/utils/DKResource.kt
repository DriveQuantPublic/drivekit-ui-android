package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.graphical.DKStyle

object DKResource {

    fun convertToDrawable(context: Context, identifier: String): Drawable? {
        val id = context.resources.getIdentifier(identifier, "drawable", context.packageName)
        return if (id > 0) {
            ContextCompat.getDrawable(context, id)
        } else {
            null
        }
    }

    fun convertToString(context: Context, identifier: String): String {
        val id = context.resources.getIdentifier(identifier, "string", context.packageName)
        return if (id > 0) {
            context.getString(id)
        } else {
            identifier
        }
    }

    fun buildString(
        context: Context,
        @ColorInt textColor: Int,
        @ColorInt highlightColor: Int,
        @StringRes identifier: Int,
        vararg args: String,
        @DimenRes textSize: Int = R.dimen.dk_text_normal,
        @DimenRes highlightSize: Int = R.dimen.dk_text_normal
    ): Spannable {
        val textArgs = args.map { TextArg(it, highlightColor, highlightSize) }.toTypedArray()
        return buildString(
            context,
            identifier,
            textColor,
            textSize,
            *textArgs,
        )
    }

    fun buildString(
        context: Context,
        @StringRes identifier: Int,
        @ColorInt textColor: Int,
        vararg args: TextArg
    ): Spannable = buildString(
        context,
        context.getString(identifier),
        textColor,
        R.dimen.dk_text_normal,
        *args
    )

    fun buildString(
        context: Context,
        @StringRes identifier: Int,
        @ColorInt textColor: Int,
        @DimenRes textSize: Int,
        vararg args: TextArg
    ): Spannable = buildString(
        context,
        context.getString(identifier),
        textColor,
        textSize,
        *args
    )

    fun buildString(
        context: Context,
        string: String,
        @ColorInt textColor: Int,
        vararg args: TextArg
    ): Spannable = buildString(
        context,
        string,
        textColor,
        R.dimen.dk_text_normal,
        *args
    )

    fun buildString(
        context: Context,
        string: String,
        @ColorInt textColor: Int,
        @DimenRes textSize: Int,
        vararg args: TextArg
    ): Spannable {
        val dkSpannable = DKSpannable()
        var currentArgPosition = 0
        val delimiters = mutableListOf<String>()
        if (args.size == 1) {
            delimiters.add("%s")
        } else {
            for (i in args.indices) {
                delimiters.add("%${i + 1}\$s")
            }
        }
        val array = string.split(*delimiters.toTypedArray())
        for ((index, value) in array.withIndex()) {
            if (value.isBlank() && currentArgPosition < args.size) {
                val currentArg = args[currentArgPosition]
                dkSpannable.append(currentArg.text, context.resSpans {
                    currentArg.color?.let { color(it) }
                    typeface(currentArg.style)
                    currentArg.size?.let { size(it) }
                })
                currentArgPosition++
            } else {
                dkSpannable.append(array[index], context.resSpans {
                    color(textColor)
                    typeface(Typeface.NORMAL)
                    size(textSize)
                })
                if (currentArgPosition < args.size) {
                    val currentArg = args[currentArgPosition]
                    if (currentArg.text.isNotBlank()) {
                        dkSpannable.append(currentArg.text, context.resSpans {
                            currentArg.color?.let { color(it) }
                            typeface(currentArg.style)
                            currentArg.size?.let { size(it) }
                        })
                        currentArgPosition++
                    }
                }
            }
        }
        return dkSpannable.toSpannable()
    }

}

data class TextArg(
    val text: String,
    @ColorInt val color: Int? = null,
    @DimenRes val size: Int? = null,
    val style: Int = Typeface.BOLD
) {
    constructor(
        text: String,
        @ColorInt color: Int? = null,
        style: DKStyle
    ) : this(text, color, style.dimensionId(), style.typefaceStyle())
}
