package com.drivequant.drivekit.ui.commons.enums

enum class DKRoadCondition {
    TRAFFIC_JAM,
    HEAVY_URBAN_TRAFFIC,
    CITY,
    SUBURBAN,
    EXPRESSWAYS;

    companion object {
        fun getTypeFromValue(value: Int): DKRoadCondition = when (value){
            0 -> TRAFFIC_JAM
            1 -> HEAVY_URBAN_TRAFFIC
            2 -> CITY
            3 -> SUBURBAN
            4 -> EXPRESSWAYS
            else -> TRAFFIC_JAM
        }
    }

    fun getValue(): Int = when (this){
        TRAFFIC_JAM -> 0
        HEAVY_URBAN_TRAFFIC -> 1
        CITY -> 2
        SUBURBAN -> 3
        EXPRESSWAYS -> 4
    }
}