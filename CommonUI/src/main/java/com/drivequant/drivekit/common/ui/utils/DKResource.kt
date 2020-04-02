package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.Spannable
import com.drivequant.drivekit.common.ui.DriveKitUI
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

    fun buildString(context: Context, identifier: String, vararg args: String): Spannable {
        val delimiter = "%s"
        val dkSpannable = DKSpannable()
        var currentArgPosition = 0
        convertToString(context, identifier)?.let { rawString ->
            val array = rawString.split(delimiter)

            for ((index, value) in array.withIndex()) {
                if (value.isBlank() && currentArgPosition < args.size) {
                    dkSpannable.append(args[currentArgPosition], context.resSpans {
                        color(DriveKitUI.colors.mainFontColor())
                        typeface(Typeface.BOLD)
                        size(R.dimen.dk_text_normal)
                    })
                    currentArgPosition++
                } else {
                    dkSpannable.append(array[index], context.resSpans {
                        color(DriveKitUI.colors.mainFontColor())
                        typeface(Typeface.NORMAL)
                        size(R.dimen.dk_text_normal)
                    })
                    if (currentArgPosition < args.size && args[currentArgPosition].isNotBlank()) {
                        dkSpannable.append(args[currentArgPosition], context.resSpans {
                            color(DriveKitUI.colors.mainFontColor())
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
