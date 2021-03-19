package com.drivequant.drivekit.common.ui.component

interface DKGaugeType {
    fun getMaxScore(): Double
    fun getColor(value: Double): Int
    fun getIcon(): Int?
    fun getGaugeConfiguration(): GaugeConfiguration
}

sealed class GaugeConfiguration {
    data class ICON(val icon: Int) : GaugeConfiguration()
    object FULL: GaugeConfiguration()

    internal fun getStartAngle(): Float = when (this){
        is ICON -> 38F
        is FULL -> 270F
    }

    internal fun getOpenAngle(): Float = when (this){
        is FULL -> 360F
        is ICON -> 128F
    }
}