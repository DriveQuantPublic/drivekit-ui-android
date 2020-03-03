package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.databaseutils.entity.DriverDistraction
import com.drivequant.drivekit.ui.R
import java.io.Serializable

class DriverDistractionViewModel(private val distraction: DriverDistraction) : Serializable {

    fun getScore() = distraction.score

    fun getUnlockNumberEvent() = distraction.nbUnlock.toString()

    fun getUnlockDuration(context: Context): SpannableString {
        return when {
            distraction.durationUnlock < 60 -> {
                val text = distraction.durationUnlock.removeZeroDecimal()

                DKSpannable().appendSpace(text, before = false)
                    .append(context.getString(R.string.dk_common_unit_second), context.resSpans {
                        appearance(R.style.UnitText)
                    }).toSpannable()
            }
            distraction.durationUnlock < 3600 -> {
                val minutes = (distraction.durationUnlock / 60).toInt()
                val seconds = (distraction.durationUnlock - (minutes * 60)).toInt()

                DKSpannable().append("$minutes")
                    .append(context.getString(R.string.dk_common_unit_minute), context.resSpans {
                        appearance(R.style.UnitText)
                    }).append("$seconds")
                    .append(context.getString(R.string.dk_common_unit_second), context.resSpans {
                        appearance(R.style.UnitText)
                    }).toSpannable()
            }
            else -> {
                val hours = (distraction.durationUnlock / 3600).toInt()
                val minutes = ((distraction.durationUnlock - (hours * 3600)) / 60).toInt()

                DKSpannable().append("$hours")
                    .append(context.getString(R.string.dk_common_unit_hour), context.resSpans {
                        appearance(R.style.UnitText)
                    }).append("$minutes").toSpannable()
            }
        }
    }

    fun getUnlockDistance(context: Context) : Spannable {
        return when {
            distraction.distanceUnlock < 1000 -> {
                val text = "${distraction.distanceUnlock.toInt()} "
                return DKSpannable().appendSpace(text, before = false).append(context.getString(R.string.dk_common_unit_meter), context.resSpans {
                    custom(TextAppearanceSpan(context, R.style.UnitText))
                }).toSpannable()
            }
            else -> {
                getDistance(context, distraction.distanceUnlock / 1000.0, context.getString(R.string.dk_common_unit_kilometer))
            }
        }
    }

    private fun getDistance(context: Context, distanceUnlock: Double, unitString: String) :SpannableString {
        val text = distanceUnlock.removeZeroDecimal()
        return DKSpannable().appendSpace(text, before = false).append(unitString, context.resSpans {
            custom(TextAppearanceSpan(context, R.style.UnitText))
        }).toSpannable()
    }
}
