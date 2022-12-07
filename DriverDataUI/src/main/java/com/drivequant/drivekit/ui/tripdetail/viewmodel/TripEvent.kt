package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripEventType.*
import java.util.*

class TripEvent(val type: TripEventType,
                val time: Date,
                val latitude: Double,
                val longitude: Double,
                val isHigh: Boolean = false,
                val value: Double = 0.0,
                val isForbidden: Boolean = false) {

    fun getEventImageResource() : Int {
        return when(type){
            SAFETY_BRAKE -> R.drawable.dk_common_safety_decel
            START -> R.drawable.dk_departure
            FINISH -> R.drawable.dk_arrival
            SAFETY_ADHERENCE -> R.drawable.dk_common_safety_adherence
            SAFETY_ACCEL -> R.drawable.dk_common_safety_accel
            PHONE_DISTRACTION_LOCK -> R.drawable.dk_lock_event
            PHONE_DISTRACTION_UNLOCK -> R.drawable.dk_unlock_event
            PHONE_DISTRACTION_PICK_UP -> R.drawable.dk_common_call
            PHONE_DISTRACTION_HANG_UP -> R.drawable.dk_end_call
        }
    }

    fun getMapImageResource() : Int {
        return when(type){
            SAFETY_BRAKE -> if (isHigh) R.drawable.dk_map_decel_high else R.drawable.dk_map_decel
            START -> R.drawable.dk_map_departure
            FINISH -> R.drawable.dk_map_arrival
            SAFETY_ADHERENCE -> if (isHigh) R.drawable.dk_map_adh_high else R.drawable.dk_map_adh
            SAFETY_ACCEL -> if (isHigh) R.drawable.dk_map_accel_high else R.drawable.dk_map_accel
            PHONE_DISTRACTION_LOCK -> R.drawable.dk_map_lock
            PHONE_DISTRACTION_UNLOCK -> R.drawable.dk_map_unlock
            PHONE_DISTRACTION_PICK_UP -> R.drawable.dk_map_begin_call
            PHONE_DISTRACTION_HANG_UP -> R.drawable.dk_map_end_call
        }
    }

    fun getTitle(context: Context) : String {
        return context.getString(when(type){
            SAFETY_BRAKE -> if (isHigh) R.string.dk_driverdata_safety_list_brake_critical else R.string.dk_common_ecodriving_decel_strong
            START -> R.string.dk_driverdata_start_event
            FINISH -> R.string.dk_driverdata_end_event
            SAFETY_ADHERENCE -> if (isHigh) R.string.dk_driverdata_safety_list_adherence_critical else R.string.dk_driverdata_safety_list_adherence
            SAFETY_ACCEL -> if (isHigh) R.string.dk_driverdata_safety_list_acceleration_critical else R.string.dk_common_ecodriving_accel_strong
            PHONE_DISTRACTION_LOCK -> R.string.dk_driverdata_lock_event
            PHONE_DISTRACTION_UNLOCK -> R.string.dk_driverdata_unlock_event
            PHONE_DISTRACTION_PICK_UP -> if (isForbidden) R.string.dk_driverdata_beginning_unauthorized_call else R.string.dk_driverdata_beginning_authorized_call
            PHONE_DISTRACTION_HANG_UP -> if (isForbidden) R.string.dk_driverdata_end_unauthorized_call else R.string.dk_driverdata_end_authorized_call
        })
    }

    fun getExplanation(context: Context) : String{
        return context.getString(when(type){
            SAFETY_BRAKE -> if (isHigh) R.string.dk_driverdata_safety_explain_brake_critical else R.string.dk_driverdata_safety_explain_brake
            START -> R.string.dk_common_ok
            FINISH -> R.string.dk_common_ok
            SAFETY_ADHERENCE -> if (isHigh) R.string.dk_driverdata_safety_explain_adherence_critical else R.string.dk_driverdata_safety_explain_adherence
            SAFETY_ACCEL -> if (isHigh) R.string.dk_driverdata_safety_explain_acceleration_critical else R.string.dk_driverdata_safety_explain_acceleration
            PHONE_DISTRACTION_LOCK -> R.string.dk_driverdata_screen_lock_text
            PHONE_DISTRACTION_UNLOCK -> R.string.dk_driverdata_screen_unlock_text
            PHONE_DISTRACTION_PICK_UP -> if (isForbidden) R.string.dk_driverdata_beginning_unauthorized_call_info_content else R.string.dk_driverdata_beginning_authorized_call_info_content
            PHONE_DISTRACTION_HANG_UP -> if (isForbidden) R.string.dk_driverdata_end_unauthorized_call_info_content else R.string.dk_driverdata_end_authorized_call_info_content
        })
    }

    fun getYAnchor() : Float {
        return when(type){
            SAFETY_BRAKE -> 1F
            START -> 0.5F
            FINISH -> 0.5F
            SAFETY_ADHERENCE -> 1F
            SAFETY_ACCEL -> 1F
            PHONE_DISTRACTION_LOCK -> 1F
            PHONE_DISTRACTION_UNLOCK -> 1F
            PHONE_DISTRACTION_PICK_UP -> 1F
            PHONE_DISTRACTION_HANG_UP -> 1F
        }
    }

    fun getXAnchor() : Float {
        return when(type){
            SAFETY_BRAKE -> 0.5F
            START -> 0.5F
            FINISH -> 0.5F
            SAFETY_ADHERENCE -> 0.5F
            SAFETY_ACCEL -> 0.5F
            PHONE_DISTRACTION_LOCK -> 0.5F
            PHONE_DISTRACTION_UNLOCK -> 0.5F
            PHONE_DISTRACTION_PICK_UP -> 0.5F
            PHONE_DISTRACTION_HANG_UP -> 0.5F
        }
    }

    fun getDescription(context: Context, trip: Trip) : Spannable? {
        return when(type){
            SAFETY_BRAKE, SAFETY_ADHERENCE, SAFETY_ACCEL -> {

                DKSpannable().append(context.getString(R.string.dk_driverdata_value),context.resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                    size(R.dimen.dk_text_small)
                }).append(" ${String.format("%.2f", value)} ${context.getString(R.string.dk_common_unit_accel_meter_per_second_square)}", context.resSpans {
                    color(DriveKitUI.colors.warningColor())
                    size(R.dimen.dk_text_small)
                    typeface(Typeface.BOLD)
                }).toSpannable()

            }

            START -> {

                if (trip.departureAddress.isEmpty())
                 SpannableString.valueOf(trip.departureCity)
                else
                    SpannableString.valueOf(trip.departureAddress)

            }
            FINISH ->
                if (trip.arrivalAddress.isEmpty())
                    SpannableString.valueOf(trip.arrivalCity)
                else
                    SpannableString.valueOf(trip.arrivalAddress)

            PHONE_DISTRACTION_LOCK -> null
            PHONE_DISTRACTION_UNLOCK -> null
            PHONE_DISTRACTION_PICK_UP, PHONE_DISTRACTION_HANG_UP -> {
                val duration = DKDataFormatter.formatDuration(
                    context,
                    DKDataFormatter.ceilDuration(value, 600)
                ).convertToString()
                DKSpannable().append(
                    context.getString(R.string.dk_driverdata_calling_time),
                    context.resSpans {
                        color(DriveKitUI.colors.mainFontColor())
                        size(R.dimen.dk_text_small)
                    }).append(" $duration", context.resSpans {
                    color(DriveKitUI.colors.warningColor())
                    size(R.dimen.dk_text_small)
                    typeface(Typeface.BOLD)
                }).toSpannable()
            }
        }
    }

    fun showInfoIcon() : Boolean {
        return when(type){
            SAFETY_BRAKE -> true
            START -> false
            FINISH -> false
            SAFETY_ADHERENCE -> true
            SAFETY_ACCEL -> true
            PHONE_DISTRACTION_LOCK -> true
            PHONE_DISTRACTION_UNLOCK -> true
            PHONE_DISTRACTION_PICK_UP -> true
            PHONE_DISTRACTION_HANG_UP -> true
        }
    }
}
