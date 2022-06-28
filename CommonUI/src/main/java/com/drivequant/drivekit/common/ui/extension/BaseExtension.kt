package com.drivequant.drivekit.common.ui.extension

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.common.ui.utils.ResSpans
import com.google.android.material.tabs.TabLayout


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

fun View.setDKStyle(color: Int = DriveKitUI.colors.backgroundViewColor()): View {
    FontUtils.overrideFonts(this.context, this)
    this.setBackgroundColor(color)
    return this
}

fun TabLayout.updateTabsFont() {
    val viewGroup = this.getChildAt(0) as ViewGroup
    val tabsCount = viewGroup.childCount
    for (j in 0 until tabsCount) {
        val viewGroupTab = viewGroup.getChildAt(j) as ViewGroup
        val tabChildsCount = viewGroupTab.childCount
        for (i in 0 until tabChildsCount) {
            val tabViewChild = viewGroupTab.getChildAt(i)
            if (tabViewChild is TextView) {
                tabViewChild.typeface = DriveKitUI.primaryFont(this.context)
            }
        }
    }
}