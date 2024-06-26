package com.drivequant.drivekit.common.ui.utils

import java.text.SimpleDateFormat
import java.util.Locale


private val dateFormatByPattern = mutableMapOf<DKDatePattern, SimpleDateFormat>()

enum class DKDatePattern {
    WEEK_LETTER,
    STANDARD_DATE,
    HOUR_ONLY,
    HOUR_MINUTE,
    HOUR_MINUTE_LETTER,
    FULL_DATE,
    DAY_MONTH,
    DAY_MONTH_LETTER_SHORT,
    DAY_MONTH_LETTER_SHORT_YEAR,
    DAY_MONTH_LETTER_YEAR,
    DAY_OF_MONTH,
    MONTH_YEAR,
    MONTH_LETTER_YEAR,
    MONTH_ABBREVIATION,
    YEAR_ONLY,
    YEAR,
    DAY,
    MONTH;

    fun getPattern() = when (this) {
        WEEK_LETTER -> "EEEE d MMMM"
        STANDARD_DATE -> "dd/MM/yyyy"
        HOUR_ONLY -> "HH'h'"
        HOUR_MINUTE_LETTER -> "HH'h'mm"
        HOUR_MINUTE -> "HH:mm"
        FULL_DATE -> "EEEE d MMMM yyyy"
        DAY_MONTH -> "dd/MM"
        DAY_MONTH_LETTER_SHORT -> "d MMM"
        DAY_MONTH_LETTER_SHORT_YEAR -> "d MMM yyyy"
        DAY_MONTH_LETTER_YEAR -> "d MMMM yyyy"
        DAY_OF_MONTH -> "d"
        MONTH_YEAR -> "MM/yyyy"
        MONTH_LETTER_YEAR -> "MMMM yyyy"
        MONTH_ABBREVIATION -> "MMM"
        YEAR_ONLY -> "E"
        YEAR -> "yyyy"
        DAY -> "EEEE"
        MONTH -> "M"
    }

    fun getSimpleDateFormat(): SimpleDateFormat = dateFormatByPattern.getOrPut(this) {
        SimpleDateFormat(this.getPattern(), Locale.getDefault())
    }
}
