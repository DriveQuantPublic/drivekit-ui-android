package com.drivequant.drivekit.common.ui.component.contextcards

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

interface DKContextCard {
    fun getItemsToDraw(): List<DKContextCardItem>
    fun getTitle(context: Context): String
    fun getEmptyDataDescription(context: Context): String
}

interface DKContextCardItem {
    @ColorRes fun getColorResId(): Int
    @StringRes fun getTitleResId(): Int
    fun getPercent(): Double
}
