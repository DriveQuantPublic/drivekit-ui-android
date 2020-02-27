package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.databaseutils.entity.DriverDistraction
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import java.io.Serializable

class DriverDistractionViewModel(private val distraction: DriverDistraction) : Serializable {

    fun getScore() = distraction.score

    fun getUnlockNumberEvent() = distraction.nbUnlock.toString()

    fun getUnlockDuration(context: Context) : Spannable {
        return when {
            distraction.durationUnlock < 60 -> {
                val text = "${distraction.durationUnlock.removeZeroDecimal()} ${context.getString(R.string.dk_common_unit_second)}"
                //TODO DKSpannable
                val spannable = SpannableString(text)
//                spannable.setSpan(
//                    TextAppearanceSpan(context, R.style.UnitText),
//                    text.count() - tripDetailViewConfig.durationSecondUnit.length,
//                    text.count(),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
                spannable
            }
            distraction.durationUnlock < 3600 -> {
                val minutes = (distraction.durationUnlock / 60).toInt()
                val seconds = (distraction.durationUnlock - (minutes * 60)).toInt()
                val text = "$minutes ${context.getString(R.string.dk_common_unit_minute)} $seconds"
                //TODO DKSpannable
                val spannable = SpannableString(text)
//                val index = text.indexOf(tripDetailViewConfig.durationMinUnit)
//                spannable.setSpan(
//                    TextAppearanceSpan(context, R.style.UnitText),
//                    index,
//                    index + tripDetailViewConfig.durationMinUnit.length,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
                spannable
            }
            else -> {
                val hours = (distraction.durationUnlock / 3600).toInt()
                val minutes = ((distraction.durationUnlock - (hours * 3600)) / 60).toInt()
                val text = "$hours ${context.getString(R.string.dk_common_unit_meter)} $minutes"
                val spannable = SpannableString(text)
//                val index = text.indexOf(tripDetailViewConfig.durationHourUnit)
//                //TODO DKSpannable
//                spannable.setSpan(
//                    TextAppearanceSpan(context, R.style.UnitText),
//                    index,
//                    index + tripDetailViewConfig.durationHourUnit.length,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
                spannable
            }
        }
    }

    fun getUnlockDistance(context: Context) : Spannable {
        return when {
            distraction.distanceUnlock < 1000 -> {
                val text = "${distraction.distanceUnlock.toInt()} ${context.getString(R.string.dk_common_unit_meter)}"
                val spannable = SpannableString(text)
//                val index = text.indexOf(tripDetailViewConfig.distanceMeterUnit)
//                //TODO DKSpannable
//                spannable.setSpan(
//                    TextAppearanceSpan(context, R.style.UnitText),
//                    index,
//                    index + tripDetailViewConfig.distanceMeterUnit.length,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
                spannable
            }
            else -> {
                getDistance(context, distraction.distanceUnlock / 1000.0, context.getString(R.string.dk_common_unit_kilometer))
            }
        }
    }

    private fun getDistance(context: Context, distanceUnlock: Double, unitString: String) :Spannable {
        val text = "${distanceUnlock.removeZeroDecimal()} $unitString"
        val spannable = SpannableString(text)
        val index = text.indexOf(unitString)
        //TODO DKSpannable
        spannable.setSpan(
            TextAppearanceSpan(context, R.style.UnitText),
            index,
            index + unitString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }
}
