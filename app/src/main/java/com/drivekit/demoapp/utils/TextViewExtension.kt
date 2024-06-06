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
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.graphical.DKColors

fun TextView.addInfoIconAtTheEnd(context: Context) {
    val imageInfo = ContextCompat.getDrawable(context, com.drivequant.drivekit.common.ui.R.drawable.dk_common_info)
    if (imageInfo != null) {
        imageInfo.tintDrawable(DKColors.secondaryColor)
        setImageSpanAtTheEnd(context, imageInfo, com.drivequant.drivekit.common.ui.R.dimen.dk_ic_small)
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
