package com.drivequant.drivekit.common.ui.component.contextcard

import android.content.Context
import androidx.annotation.ColorRes

interface DKContextCard {
    fun getItems(): List<DKContextCardItem>
    fun getTitle(context: Context): String
    fun getEmptyDataDescription(context: Context): String
}

interface DKContextCardItem {
    @ColorRes fun getColorResId(): Int
    fun getTitle(context: Context): String
    fun getSubtitle(context: Context): String?
    fun getPercent(): Double
}
