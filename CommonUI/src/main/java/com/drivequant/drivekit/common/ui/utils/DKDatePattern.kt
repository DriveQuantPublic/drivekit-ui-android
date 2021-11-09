package com.drivequant.drivekit.common.ui.utils


enum class DKDatePattern {

    WEEK_LETTER,
    STANDARD_DATE,
    HOUR_MINUTE,
    HOUR_MINUTE_LETTER,
    FULL_DATE,
    DAY_MONTH,
    MONTH_YEAR,
    YEAR_ONLY,
    YEAR,
    DAY,
    MONTH;

    fun getPattern() = when (this) {
        WEEK_LETTER -> "EEEE d MMMM"
        STANDARD_DATE -> "dd/MM/yyyy"
        HOUR_MINUTE_LETTER -> "HH'h'mm"
        HOUR_MINUTE -> "HH:mm"
        FULL_DATE -> "EEEE d MMMM yyyy"
        DAY_MONTH -> "dd/MM"
        MONTH_YEAR -> "MM/yyyy"
        YEAR_ONLY -> "E"
        YEAR -> "yyyy"
        DAY -> "EEEE"
        MONTH -> "M"
    }
}