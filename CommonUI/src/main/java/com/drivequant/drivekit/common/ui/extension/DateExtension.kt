@file:JvmName("DKDateUtils")
package com.drivequant.drivekit.common.ui.extension

import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import java.text.SimpleDateFormat
import java.util.*

fun Date.formatDate(pattern: DKDatePattern): String {
    val dateFormat = SimpleDateFormat(pattern.getPattern(), Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.formatDateWithPattern(sdf: SimpleDateFormat): String = sdf.format(this)

fun Date.isSameDay(date: Date): Boolean {
    val tripCal: Calendar = Calendar.getInstance()
    tripCal.time = this

    val currentDateCal: Calendar = Calendar.getInstance()
    currentDateCal.time = date

    return (tripCal.get(Calendar.DAY_OF_YEAR) == currentDateCal.get(Calendar.DAY_OF_YEAR)
            && tripCal.get(Calendar.YEAR) == currentDateCal.get(Calendar.YEAR))
}

fun Date.startingFrom(calendarField: CalendarField): Date {
    val subField = calendarField.firstSubField() ?: return this
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = this
    calendar.dkclear(subField)
    return calendar.time
}

fun Date.diffWith(date: Date, calendarField: CalendarField): Long {
    val delta = this.time - date.time
    if (calendarField == CalendarField.YEAR || calendarField == CalendarField.MONTH) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = delta
        val originCalendar = Calendar.getInstance()
        originCalendar.timeInMillis = 0
        return when (calendarField) {
            CalendarField.YEAR -> (calendar.get(Calendar.YEAR) - originCalendar.get(Calendar.YEAR)).toLong()
            else -> (calendar.get(Calendar.YEAR) - originCalendar.get(Calendar.YEAR)) * 12L + (calendar.get(Calendar.MONTH) - originCalendar.get(Calendar.MONTH))
        }
    } else {
        val timeUnit: DKTimeUnit = when (calendarField) {
            CalendarField.WEEK -> DKTimeUnit.WEEKS
            CalendarField.DAY -> DKTimeUnit.DAYS
            CalendarField.HOUR -> DKTimeUnit.HOURS
            CalendarField.MINUTE -> DKTimeUnit.MINUTES
            CalendarField.SECOND -> DKTimeUnit.SECONDS
            else -> DKTimeUnit.MILLISECONDS
        }
        return timeUnit.convert(delta, DKTimeUnit.MILLISECONDS)
    }
}

fun Date.add(value: Int, calendarField: CalendarField): Date {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(calendarField.field, value)
    return calendar.time
}

fun Date.removeTime(): Date {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = this
    calendar.clearAfter(CalendarField.DAY)
    return calendar.time
}


enum class CalendarField(val field: Int, internal val subFields: List<Int>) {
    YEAR(Calendar.YEAR, listOf(Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND)),
    MONTH(Calendar.MONTH, listOf(Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND)),
    WEEK(Calendar.WEEK_OF_YEAR, listOf(Calendar.DAY_OF_WEEK, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND)),
    DAY(Calendar.DAY_OF_MONTH, listOf(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND)),
    HOUR(Calendar.HOUR_OF_DAY, listOf(Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND)),
    MINUTE(Calendar.MINUTE, listOf(Calendar.SECOND, Calendar.MILLISECOND)),
    SECOND(Calendar.SECOND, listOf(Calendar.MILLISECOND)),
    MILLISECOND(Calendar.MILLISECOND, emptyList());

    internal fun firstSubField(): Int? {
        return this.subFields.firstOrNull()
    }
}

fun Calendar.clearAfter(calendarField: CalendarField) {
    clear(calendarField.subFields)
}

fun Calendar.clearFrom(calendarField: CalendarField) {
    val fields: MutableList<Int> = mutableListOf(calendarField.field)
    fields.addAll(calendarField.subFields)
    clear(fields)
}

private fun Calendar.clear(fields: List<Int>) {
    for (field in fields) {
        dkclear(field)
    }
}

private fun Calendar.dkclear(field: Int) {
    val clearValue: Int = when (field) {
        Calendar.DAY_OF_WEEK -> Calendar.MONDAY
        else -> this.getActualMinimum(field)
    }
    set(field, clearValue)
}

private enum class DKTimeUnit(private val scale: Long) {
    MILLISECONDS(TimeUnitScale.MILLI_SCALE),
    SECONDS(TimeUnitScale.SECOND_SCALE),
    MINUTES(TimeUnitScale.MINUTE_SCALE),
    HOURS(TimeUnitScale.HOUR_SCALE),
    DAYS(TimeUnitScale.DAY_SCALE),
    WEEKS(TimeUnitScale.WEEK_SCALE);

    private object TimeUnitScale {
        const val MILLI_SCALE = 1L
        const val SECOND_SCALE = 1000L * MILLI_SCALE
        const val MINUTE_SCALE = 60L * SECOND_SCALE
        const val HOUR_SCALE = 60L * MINUTE_SCALE
        const val DAY_SCALE = 24L * HOUR_SCALE
        const val WEEK_SCALE = 7L * DAY_SCALE
    }

    // Code extracted from java.util.concurrent.TimeUnit.
    fun convert(duration: Long, sourceUnit: DKTimeUnit): Long {
        val resultUnitScale = this.scale
        val sourceUnitScale = sourceUnit.scale
        return if (sourceUnitScale == resultUnitScale) {
            duration
        } else if (sourceUnitScale < resultUnitScale) {
            duration / (resultUnitScale / sourceUnitScale)
        } else {
            val m = Long.MAX_VALUE
            val r = sourceUnitScale / resultUnitScale
            if (duration > m / r) {
                Long.MAX_VALUE
            } else if (duration < -m) {
                Long.MIN_VALUE
            } else {
                duration * r
            }
        }
    }
}
