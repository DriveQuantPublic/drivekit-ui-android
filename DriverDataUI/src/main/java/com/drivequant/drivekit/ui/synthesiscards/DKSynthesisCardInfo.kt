package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.databaseutils.entity.TripWithRelations
import com.drivequant.drivekit.databaseutils.entity.toTrips
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.computeActiveDays
import com.drivequant.drivekit.ui.extension.computeTotalDistance
import com.drivequant.drivekit.ui.extension.computeTotalDuration

interface DKSynthesisCardInfo {
    val trips: List<TripWithRelations>
    fun getIcon(context: Context): Drawable?
    fun getText(context: Context): Spannable
}

sealed class SynthesisCardInfo : DKSynthesisCardInfo {
    data class ACTIVE_DAYS(val context: Context, override val trips: List<TripWithRelations>) :
        SynthesisCardInfo()

    data class DISTANCE(val context: Context, override val trips: List<TripWithRelations>) :
        SynthesisCardInfo()

    data class DURATION(val context: Context, override val trips: List<TripWithRelations>) :
        SynthesisCardInfo()

    data class TRIPS(val context: Context, override val trips: List<TripWithRelations>) :
        SynthesisCardInfo()

    override fun getIcon(context: Context): Drawable? {
        val identifier = when (this) {
            is ACTIVE_DAYS -> R.drawable.dk_common_calendar
            is DISTANCE -> R.drawable.dk_common_road
            is DURATION -> R.drawable.dk_common_clock
            is TRIPS -> R.drawable.dk_common_trip
        }
        return ContextCompat.getDrawable(context, identifier)
    }

    override fun getText(context: Context): Spannable {
        val value: Int
        lateinit var textIdentifier: String

        when (this) {
            is ACTIVE_DAYS -> {
                val activeDays = trips.toTrips().computeActiveDays()
                return DKSpannable().append(activeDays.toString(), context.resSpans {
                    color(DriveKitUI.colors.primaryColor())
                    typeface(Typeface.BOLD)
                    size(R.dimen.dk_text_medium)
                }).toSpannable()
            }
            is DISTANCE -> {
                val distance = DKDataFormatter.formatMeterDistanceInKm(
                    context,
                    trips.toTrips().computeTotalDistance()
                )
                return DKSpannable().append(distance, context.resSpans {
                    color(DriveKitUI.colors.primaryColor())
                    typeface(Typeface.BOLD)
                    size(R.dimen.dk_text_medium)
                }).toSpannable()
            }
            is DURATION -> {
                val duration =
                    DKDataFormatter.formatDuration(context, trips.toTrips().computeTotalDuration())
                return DKSpannable().append(duration, context.resSpans {
                    color(DriveKitUI.colors.primaryColor())
                    typeface(Typeface.BOLD)
                    size(R.dimen.dk_text_medium)
                }).toSpannable()
            }
            is TRIPS -> {
                value = trips.size
                textIdentifier = context.resources.getQuantityString(R.plurals.trip_plural, value)

                val spannable = DKSpannable()
                    .append("$value ", context.resSpans {
                        color(DriveKitUI.colors.primaryColor())
                        typeface(Typeface.BOLD)
                        size(R.dimen.dk_text_medium)
                    })
                    .appendSpace(DKResource.convertToString(context, textIdentifier))

                return spannable.toSpannable()
            }
        }
    }
}
