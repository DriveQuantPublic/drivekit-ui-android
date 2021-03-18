package com.drivequant.drivekit.ui.synthesiscards

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R

interface DKTripCardInfo {
    val context: Context
    fun getIcon(context: Context): Drawable?
    fun getText(context: Context): Spannable
}

sealed class TripCardInfo(override val context: Context) : DKTripCardInfo{
    data class COUNT(override val context: Context) : TripCardInfo(context)
    data class DISTANCE(override val context: Context) : TripCardInfo(context)
    data class DURATION(override val context: Context) : TripCardInfo(context)

    override fun getIcon(context: Context): Drawable? {
        val identifier = when (this){
            is COUNT -> R.drawable.dk_common_trip
            is DISTANCE -> R.drawable.dk_common_road
            is DURATION -> R.drawable.dk_common_clock
        }
        return ContextCompat.getDrawable(context, identifier)
    }

    override fun getText(context: Context): Spannable {
        val textColor = DriveKitUI.colors.neutralColor()
        val highlightColor = DriveKitUI.colors.primaryColor()
        val value: Int
        lateinit var textIdentifier: String


        when (this){
            is COUNT -> {
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
        }
        return DKResource.buildString(context, textColor, highlightColor, textIdentifier, value.toString())
    }
}
