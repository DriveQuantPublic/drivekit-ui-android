package com.drivequant.drivekit.common.ui.extension

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.common.ui.utils.ResSpans


inline fun Context.resSpans(options: ResSpans.() -> Unit) = ResSpans(this).apply(options)

fun Drawable.tintDrawable(color: Int) {
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            this.mutate().colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        }
        else -> {
            DrawableCompat.setTint(this, color)
        }
    }
}

fun View.setDKStyle(): View {
    FontUtils.overrideFonts(this.context, this)
    this.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    return this
}