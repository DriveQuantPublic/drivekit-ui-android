package com.drivequant.drivekit.common.ui.graphical

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.DimenRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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

    fun fontWeight(): FontWeight = when (this) {
        HEADLINE1 -> FontWeight.Bold
        HEADLINE2 -> FontWeight.Bold
        BIG_TEXT -> FontWeight.Normal
        NORMAL_TEXT -> FontWeight.Normal
        SMALL_TEXT -> FontWeight.Normal
        HIGHLIGHT_BIG -> FontWeight.Bold
        HIGHLIGHT_NORMAL -> FontWeight.Bold
        HIGHLIGHT_SMALL -> FontWeight.Bold
    }

    companion object {
        val primaryFont = FontFamily(Font(R.font.dkprimary))
        val secondaryFont = FontFamily(Font(R.font.dksecondary))
    }

    @Composable
    fun DKTextStyle(fontFamily: FontFamily = primaryFont): TextStyle {
        return TextStyle(
            fontFamily = fontFamily,
            fontWeight = this.fontWeight(),
            fontSize = dimensionResource(this.dimensionId()).value.sp,
            color = colorResource(R.color.mainFontColor),
        )
    }
}
