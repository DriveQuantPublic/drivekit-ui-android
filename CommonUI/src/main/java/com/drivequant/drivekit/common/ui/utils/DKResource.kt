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
        @StringRes identifier: Int,
        @DimenRes textSize: Int = R.dimen.dk_text_normal,
    ): Spannable {
        return buildString(
            context,
            identifier,
            textColor,
            textSize,
            *emptyArray<Arg>(),
        )
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
        @ColorInt textColor: Int,
        @ColorInt highlightColor: Int,
        @StringRes identifier: Int,
        vararg args: Int,
        @DimenRes textSize: Int = R.dimen.dk_text_normal,
        @DimenRes highlightSize: Int = R.dimen.dk_text_normal
    ): Spannable {
        val intArgs = args.map { IntArg(it, highlightColor, highlightSize) }.toTypedArray()
        return buildString(
            context,
            identifier,
            textColor,
            textSize,
            *intArgs,
        )
    }

    fun buildString(
        context: Context,
        @StringRes identifier: Int,
        @ColorInt textColor: Int,
        vararg args: Arg
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
        vararg args: Arg
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
        vararg args: Arg
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
        vararg args: Arg
    ): Spannable {
        val dkSpannable = DKSpannable()
        var currentArgPosition = 0
        val delimiters = mutableListOf<String>()
        if (args.size == 1) {
            delimiters.add("%${args.first().formatChar}")
        } else {
            for ((i, arg) in args.withIndex()) {
                delimiters.add("%${i + 1}\$${arg.formatChar}")
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

abstract class Arg(
    open val text: String,
    open val color: Int?,
    open val size: Int?,
    open val style: Int,
    internal val formatChar: Char
)

data class TextArg(
    override val text: String,
    @ColorInt override val color: Int? = null,
    @DimenRes override val size: Int? = null,
    override val style: Int = Typeface.BOLD
) : Arg(text, color, size, style, 's') {
    constructor(
        text: String,
        @ColorInt color: Int? = null,
        style: DKStyle
    ) : this(text, color, style.dimensionId(), style.typefaceStyle())
}

data class IntArg(
    val value: Int,
    @ColorInt override val color: Int? = null,
    @DimenRes override val size: Int? = null,
    override val style: Int = Typeface.BOLD
) : Arg(value.toString(), color, size, style, 'd') {
    constructor(
        value: Int,
        @ColorInt color: Int? = null,
        style: DKStyle
    ) : this(value, color, style.dimensionId(), style.typefaceStyle())
}
