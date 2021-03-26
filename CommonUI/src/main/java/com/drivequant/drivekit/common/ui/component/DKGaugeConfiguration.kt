package com.drivequant.drivekit.common.ui.component

import android.content.Context

interface DKGaugeConfiguration {
    fun getTitle(context: Context): String
    fun getColor(value: Double): Int
    fun getMaxScore(): Double
    fun getIcon(): Int?
    fun getGaugeConfiguration(): DKGaugeType
}

sealed class DKGaugeType {
    data class OPEN_WITH_IMAGE(val icon: Int) : DKGaugeType()
    object CLOSE: DKGaugeType()

    internal fun getStartAngle(): Float = when (this){
        is OPEN_WITH_IMAGE -> 38F
        is CLOSE -> 270F
    }

    internal fun getOpenAngle(): Float = when (this){
        is CLOSE -> 360F
        is OPEN_WITH_IMAGE -> 128F
    }
}