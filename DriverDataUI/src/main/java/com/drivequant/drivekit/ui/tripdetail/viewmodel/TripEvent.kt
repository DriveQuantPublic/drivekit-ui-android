package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import java.util.*

class TripEvent(val type: TripEventType,
                val time: Date,
                val latitude: Double,
                val longitude: Double,
                val isHigh: Boolean = false,
                val value: Double = 0.0){

    fun getEventImageResource() : Int {
        return when(type){
            TripEventType.SAFETY_BRAKE -> R.drawable.dk_safety_decel
            TripEventType.START -> R.drawable.dk_start_event_black
            TripEventType.FINISH -> R.drawable.dk_end_event_black
            TripEventType.SAFETY_ADHERENCE -> R.drawable.dk_safety_adherence
            TripEventType.SAFETY_ACCEL -> R.drawable.dk_safety_accel
            TripEventType.PHONE_DISTRACTION_LOCK -> R.drawable.dk_lock_event
            TripEventType.PHONE_DISTRACTION_UNLOCK -> R.drawable.dk_unlock_event
        }
    }

    fun getMapImageResource() : Int {
        return when(type){
            TripEventType.SAFETY_BRAKE -> if (isHigh) R.drawable.dk_map_decel_high else R.drawable.dk_map_decel
            TripEventType.START -> R.drawable.dk_map_start_event
            TripEventType.FINISH -> R.drawable.dk_map_end_event
            TripEventType.SAFETY_ADHERENCE -> if (isHigh) R.drawable.dk_map_adh_high else R.drawable.dk_map_adh
            TripEventType.SAFETY_ACCEL -> if (isHigh) R.drawable.dk_map_accel_high else R.drawable.dk_map_accel
            TripEventType.PHONE_DISTRACTION_LOCK -> R.drawable.dk_map_lock
            TripEventType.PHONE_DISTRACTION_UNLOCK -> R.drawable.dk_map_unlock
        }
    }

    fun getTitle(detailViewConfig: TripDetailViewConfig) : String{
        return when(type){
            TripEventType.SAFETY_BRAKE -> if (isHigh) detailViewConfig.eventDecelCritText else detailViewConfig.eventDecelText
            TripEventType.START -> detailViewConfig.startText
            TripEventType.FINISH -> detailViewConfig.endText
            TripEventType.SAFETY_ADHERENCE -> if (isHigh) detailViewConfig.eventAdherenceCritText else detailViewConfig.eventAdherenceText
            TripEventType.SAFETY_ACCEL -> if (isHigh) detailViewConfig.eventAccelCritText else detailViewConfig.eventAccelText
            TripEventType.PHONE_DISTRACTION_LOCK -> detailViewConfig.lockText
            TripEventType.PHONE_DISTRACTION_UNLOCK -> detailViewConfig.unlockText
        }
    }

    fun getExplanation(context: Context) : String{
        return context.getString(when(type){
            TripEventType.SAFETY_BRAKE -> if (isHigh) R.string.dk_safety_explain_brake_critical else R.string.dk_safety_explain_brake
            TripEventType.START -> R.string.dk_ok
            TripEventType.FINISH -> R.string.dk_ok
            TripEventType.SAFETY_ADHERENCE -> if (isHigh) R.string.dk_safety_explain_adherence_critical else R.string.dk_safety_explain_adherence
            TripEventType.SAFETY_ACCEL -> if (isHigh) R.string.dk_safety_explain_acceleration_critical else R.string.dk_safety_explain_acceleration
            TripEventType.PHONE_DISTRACTION_LOCK -> R.string.dk_screen_lock_text
            TripEventType.PHONE_DISTRACTION_UNLOCK -> R.string.dk_screen_unlock_text
        })
    }

    fun getYAnchor() : Float {
        return when(type){
            TripEventType.SAFETY_BRAKE -> 1F
            TripEventType.START -> 0.5F
            TripEventType.FINISH -> 0.5F
            TripEventType.SAFETY_ADHERENCE -> 1F
            TripEventType.SAFETY_ACCEL -> 1F
            TripEventType.PHONE_DISTRACTION_LOCK -> 1F
            TripEventType.PHONE_DISTRACTION_UNLOCK -> 1F
        }
    }

    fun getXAnchor() : Float {
        return when(type){
            TripEventType.SAFETY_BRAKE -> 0.5F
            TripEventType.START -> 0.5F
            TripEventType.FINISH -> 0.5F
            TripEventType.SAFETY_ADHERENCE -> 0.5F
            TripEventType.SAFETY_ACCEL -> 0.5F
            TripEventType.PHONE_DISTRACTION_LOCK -> 0.5F
            TripEventType.PHONE_DISTRACTION_UNLOCK -> 0.5F
        }
    }

    fun getDescription(context: Context, trip: Trip) : String? {
        return when(type){
            TripEventType.SAFETY_BRAKE -> "${context.getString(R.string.dk_value)} ${String.format("%.2f", value)} ${context.getString(R.string.dk_unit_accel)}"
            TripEventType.START -> if (trip.departureAddress.isNullOrEmpty()) trip.departureCity else trip.departureAddress
            TripEventType.FINISH ->if (trip.arrivalAddress.isNullOrEmpty()) trip.arrivalCity else trip.arrivalAddress
            TripEventType.SAFETY_ADHERENCE -> "${context.getString(R.string.dk_value)} ${String.format("%.2f", value)} ${context.getString(R.string.dk_unit_accel)}"
            TripEventType.SAFETY_ACCEL -> "${context.getString(R.string.dk_value)} ${String.format("%.2f", value)} ${context.getString(R.string.dk_unit_accel)}"
            TripEventType.PHONE_DISTRACTION_LOCK -> null
            TripEventType.PHONE_DISTRACTION_UNLOCK -> null
        }
    }

    fun showInfoIcon() : Boolean {
        return when(type){
            TripEventType.SAFETY_BRAKE -> true
            TripEventType.START -> false
            TripEventType.FINISH -> false
            TripEventType.SAFETY_ADHERENCE -> true
            TripEventType.SAFETY_ACCEL -> true
            TripEventType.PHONE_DISTRACTION_LOCK -> true
            TripEventType.PHONE_DISTRACTION_UNLOCK -> true
        }
    }
}
