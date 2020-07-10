package com.drivequant.drivekit.driverachievement.ui.leaderboard

import android.view.View
import android.view.ViewGroup

fun View.setMarginLeft(leftMargin: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(leftMargin, params.topMargin, params.rightMargin, params.bottomMargin)
    layoutParams = params
}

fun View.setMarginRight(rightMargin: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(params.leftMargin, rightMargin, params.rightMargin, params.bottomMargin)
    layoutParams = params
}