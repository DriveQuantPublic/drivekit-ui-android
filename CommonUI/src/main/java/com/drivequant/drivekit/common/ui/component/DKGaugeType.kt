package com.drivequant.drivekit.common.ui.component

interface DKGaugeType {
    fun getColor(value: Double): Int
    fun getIcon(): Int?
    fun getGaugeConfiguration(): GaugeConfiguration
}

enum class GaugeConfiguration {
    FULL, ICON;

    internal fun getStartAngle(): Float = when (this){
        FULL -> 270F
        ICON -> 38F
    }

    internal fun getOpenAngle(): Float = when (this){
        FULL -> 360F
        ICON -> 128F
    }
}