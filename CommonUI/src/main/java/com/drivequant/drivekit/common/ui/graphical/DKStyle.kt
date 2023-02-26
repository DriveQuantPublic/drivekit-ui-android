package com.drivequant.drivekit.common.ui.graphical

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R

enum class DKStyle {
    HEADLINE1,
    HEADLINE2,
    BIG_TEXT,
    NORMAL_TEXT,
    SMALL_TEXT,
    HIGHLIGHT_BIG,
    HIGHLIGHT_NORMAL,
    HIGHLIGHT_SMALL;

    fun size(context: Context): Float = context.resources.getDimension(dimensionId())

    @DimenRes
    fun dimensionId(): Int = when (this) {
        HEADLINE1 -> R.dimen.dk_text_medium
        HEADLINE2 -> R.dimen.dk_text_normal
        BIG_TEXT -> R.dimen.dk_text_medium
        NORMAL_TEXT -> R.dimen.dk_text_normal
        SMALL_TEXT -> R.dimen.dk_text_small
        HIGHLIGHT_BIG -> R.dimen.dk_text_xxbig
        HIGHLIGHT_NORMAL -> R.dimen.dk_text_xbig
        HIGHLIGHT_SMALL -> R.dimen.dk_text_big
    }

    fun typefaceStyle(): Int = when (this) {
        HEADLINE1 -> Typeface.BOLD
        HEADLINE2 -> Typeface.BOLD
        BIG_TEXT -> Typeface.NORMAL
        NORMAL_TEXT -> Typeface.NORMAL
        SMALL_TEXT -> Typeface.NORMAL
        HIGHLIGHT_BIG -> Typeface.BOLD
        HIGHLIGHT_NORMAL -> Typeface.BOLD
        HIGHLIGHT_SMALL -> Typeface.BOLD
    }
}
