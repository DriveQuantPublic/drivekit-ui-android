package com.drivequant.drivekit.driverachievement.ui.utils

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.Spanned

import com.drivequant.drivekit.driverachievement.ui.R

object HtmlUtils {
    fun getTextHighlight(text: String, context: Context): String {
        return "<span style=\"color:" + ContextCompat.getColor(
            context,
            R.color.dk_primary_color
        ) + "\">" + text + "</span>"
    }

    fun getTextBigAndBold(text: String): String = "<b><big>$text</big></b>"

    fun fromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }
}
