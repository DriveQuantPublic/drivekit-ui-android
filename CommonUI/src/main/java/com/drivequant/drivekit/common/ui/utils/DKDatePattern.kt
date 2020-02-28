package com.drivequant.drivekit.common.ui.utils


enum class DKDatePattern {

    FORMAT_WEEK_LETTER,
    FORMAT_STANDARD_DATE,
    FORMAT_HOUR_MINUTE,
    FORMAT_HOUR_MINUTE_LETTER,
    FORMAT_FULL_DATE,
    FORMAT_DAY_MONTH,
    FORMAT_YEAR_ONLY,
    FORMAT_YEAR,
    FORMAT_DAY,
    FORMAT_MONTH;

    fun getPattern(): String {
        return when (this) {
            FORMAT_WEEK_LETTER -> "EEEE d MMMM"
            FORMAT_STANDARD_DATE -> "dd/MM/yyyy"
            FORMAT_HOUR_MINUTE_LETTER -> "HH'h'mm"
            FORMAT_HOUR_MINUTE -> "HH:mm"
            FORMAT_FULL_DATE -> "EEEE d MMMM yyyy"
            FORMAT_DAY_MONTH -> "dd/MM"
            FORMAT_YEAR_ONLY -> "E"
            FORMAT_YEAR -> "yyyy"
            FORMAT_DAY -> "EEEE"
            FORMAT_MONTH -> "M"
        }
    }
}