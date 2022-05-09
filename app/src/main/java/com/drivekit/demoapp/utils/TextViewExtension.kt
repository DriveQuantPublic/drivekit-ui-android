package com.drivekit.demoapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI

fun TextView.addInfoIconAtTheEnd(context: Context) {
    val imageInfo = ContextCompat.getDrawable(context, R.drawable.dk_common_info)
    if (imageInfo != null) {
        imageInfo.mutate()
        DrawableCompat.setTint(imageInfo, DriveKitUI.colors.secondaryColor())
        setImageSpanAtTheEnd(context, imageInfo, R.dimen.dk_ic_small)
    }
}

fun TextView.setImageSpanAtTheEnd(context: Context, drawable: Drawable, @DimenRes dimenResId: Int) {
    text = SpannableString("$text ").apply {
        val margin = 20
        val size = context.resources.getDimension(dimenResId).toInt()
        drawable.setBounds(margin, 0, size + margin, size)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan.ALIGN_CENTER
        } else {
            ImageSpan.ALIGN_BASELINE
        }.let {
            setSpan(ImageSpan(drawable, it), text.length, text.length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}