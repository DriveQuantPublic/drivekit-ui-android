package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.text.Spannable
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.resSpans

object DKResource {

    fun convertToDrawable(context: Context, identifier: String): Drawable? {
        val id = context.resources.getIdentifier(identifier, "drawable", context.packageName)
        return if (id > 0){
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
        textColor: Int,
        highlightColor: Int,
        identifier: String,
        vararg args: String
    ): Spannable {
        val dkSpannable = DKSpannable()
        var currentArgPosition = 0
        val delimiters = mutableListOf<String>()
        args.forEachIndexed { index, _ ->
            if (args.size == 1) {
                delimiters.add("%s")
            } else {
                delimiters.add("%${index + 1}\$s")
            }
        }
        convertToString(context, identifier).let { rawString ->
            val array = rawString.split(*delimiters.toTypedArray())

            for ((index, value) in array.withIndex()) {
                if (value.isBlank() && currentArgPosition < args.size) {
                    dkSpannable.append(args[currentArgPosition], context.resSpans {
                        color(highlightColor)
                        typeface(Typeface.BOLD)
                        size(R.dimen.dk_text_normal)
                    })
                    currentArgPosition++
                } else {
                    dkSpannable.append(array[index], context.resSpans {
                        color(textColor)
                        typeface(Typeface.NORMAL)
                        size(R.dimen.dk_text_normal)
                    })
                    if (currentArgPosition < args.size && args[currentArgPosition].isNotBlank()) {
                        dkSpannable.append(args[currentArgPosition], context.resSpans {
                            color(highlightColor)
                            typeface(Typeface.BOLD)
                            size(R.dimen.dk_text_normal)
                        })
                        currentArgPosition++
                    }
                }
            }
        }
        return dkSpannable.toSpannable()
    }
}
