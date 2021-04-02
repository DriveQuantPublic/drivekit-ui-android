package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.TripWithRelations
import com.drivequant.drivekit.ui.R

interface DKSynthesisCardInfo {
    val trips: List<TripWithRelations>
    fun getIcon(context: Context): Drawable?
    fun getText(context: Context): Spannable
}

sealed class SynthesisCardInfo : DKSynthesisCardInfo {
    data class ACTIVE_DAYS(val context: Context, override val trips: List<TripWithRelations>) : SynthesisCardInfo()
    data class DISTANCE(val context: Context, override val trips: List<TripWithRelations>) : SynthesisCardInfo()
    data class DURATION(val context: Context, override val trips: List<TripWithRelations>) : SynthesisCardInfo()
    data class TRIPS(val context: Context, override val trips: List<TripWithRelations>) : SynthesisCardInfo()

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
        val textColor = DriveKitUI.colors.neutralColor()
        val highlightColor = DriveKitUI.colors.primaryColor()
        val value: Int
        lateinit var textIdentifier: String

        when (this) {
            is ACTIVE_DAYS -> {
                value = 12
                textIdentifier = "dk_common_trip_singular"
            }
            is DISTANCE -> {
                value = 12
                textIdentifier = "dk_common_trip_singular"
            }
            is DURATION -> {
                value = 12
                textIdentifier = "dk_common_trip_singular"
            }
            is TRIPS -> {
                value = trips.size
                textIdentifier = "dk_common_trip_singular"
            }
        }
        return DKResource.buildString(
            context,
            textColor,
            highlightColor,
            textIdentifier,
            value.toString()
        )
    }
}
