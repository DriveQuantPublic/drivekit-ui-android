package com.drivequant.drivekit.common.ui.extension

import android.content.Context
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.common.ui.utils.ResSpans

inline fun Context.resSpans(options: ResSpans.() -> Unit) = ResSpans(this).apply(options)

fun View.setDKStyle(): View {
    FontUtils.overrideFonts(this.context, this)
    this.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    return this
}