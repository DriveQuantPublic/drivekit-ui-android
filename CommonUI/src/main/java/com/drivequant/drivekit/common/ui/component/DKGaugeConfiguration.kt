package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.text.Spannable

interface DKGaugeConfiguration {
    fun getTitle(context: Context): Spannable
    fun getScore(): Double
    fun getColor(value: Double): Int
    fun getMaxScore(): Double
    fun getIcon(): Int?
    fun getGaugeType(): DKGaugeType
}

sealed class DKGaugeType {
    object CLOSED: DKGaugeType()
    object OPEN: DKGaugeType()
    data class OPEN_WITH_IMAGE(val icon: Int) : DKGaugeType()

    internal fun getStartAngle(): Float = when (this){
        is CLOSED -> 270F
        is OPEN -> 38F
        is OPEN_WITH_IMAGE -> 38F
    }

    internal fun getOpenAngle(): Float = when (this){
        is CLOSED -> 0F
        is OPEN -> 128F
        is OPEN_WITH_IMAGE -> 128F
    }
}