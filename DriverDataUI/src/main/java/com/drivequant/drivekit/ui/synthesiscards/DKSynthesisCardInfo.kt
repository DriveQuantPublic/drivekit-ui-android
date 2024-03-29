package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DurationUnit
import com.drivequant.drivekit.common.ui.utils.FormatType
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.extension.computeActiveDays
import com.drivequant.drivekit.ui.extension.computeTotalDistance
import com.drivequant.drivekit.ui.extension.computeTotalDuration

interface DKSynthesisCardInfo {
    fun getIcon(context: Context): Drawable?
    fun getText(context: Context): List<FormatType>
}

sealed class SynthesisCardInfo(open val trips: List<Trip>) : DKSynthesisCardInfo {
    data class ACTIVEDAYS(override val trips: List<Trip>) :
        SynthesisCardInfo(trips)

    data class DISTANCE(override val trips: List<Trip>) :
        SynthesisCardInfo(trips)

    data class DURATION(override val trips: List<Trip>) :
        SynthesisCardInfo(trips)

    data class TRIPS(override val trips: List<Trip>) :
        SynthesisCardInfo(trips)

    override fun getIcon(context: Context): Drawable? {
        val identifier = when (this) {
            is ACTIVEDAYS -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_calendar
            is DISTANCE -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_road
            is DURATION -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_clock
            is TRIPS -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_trip
        }
        return ContextCompat.getDrawable(context, identifier)
    }

    override fun getText(context: Context): List<FormatType> {
        var formattingTypes = listOf<FormatType>()
        when (this) {
            is ACTIVEDAYS -> {
                val activeDays = trips.computeActiveDays()
                val daysString = context.resources.getQuantityString(
                    com.drivequant.drivekit.common.ui.R.plurals.day_plural,
                    activeDays
                )
                formattingTypes = listOf(
                    FormatType.VALUE(activeDays.toString()),
                    FormatType.SEPARATOR(),
                    FormatType.UNIT(daysString)
                )
            }
            is DISTANCE -> {
                val computedDistance = DKDataFormatter.ceilDistance(trips.computeTotalDistance(), 10000)
                formattingTypes = DKDataFormatter.getMeterDistanceFormat(context, computedDistance)
            }
            is DURATION -> {
                val computedDuration = DKDataFormatter.ceilDuration(trips.computeTotalDuration(), 600)
                formattingTypes = DKDataFormatter.formatDuration(context, computedDuration, DurationUnit.HOUR)
            }
            is TRIPS -> {
                val value = trips.size
                val text = context.resources.getQuantityString(com.drivequant.drivekit.common.ui.R.plurals.trip_plural, value)

                formattingTypes = listOf(
                    FormatType.VALUE(value.toString()),
                    FormatType.SEPARATOR(),
                    FormatType.UNIT(text)
                )
            }
        }
        return formattingTypes
    }
}
