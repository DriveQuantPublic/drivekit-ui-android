package com.drivequant.drivekit.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.trips.viewmodel.HeaderSummary
import java.io.Serializable

class TripDetailViewConfig(
    context: Context,
    val mapItems: List<MapItem> = listOf(MapItem.SAFETY, MapItem.ECO_DRIVING, MapItem.DISTRACTION, MapItem.INTERACTIVE_MAP),
    val headerSummary: HeaderSummary = HeaderSummary.DURATION_DISTANCE,
    val displayAdvices: Boolean = true,
    val viewTitleText: String = context.getString(R.string.dk_trip_detail_title),
    val mapTraceMainColor: Int = ContextCompat.getColor(context, R.color.dkMapTraceMainColor),
    val mapTraceWarningColor: Int = ContextCompat.getColor(context, R.color.dkMapTraceWarningColor),
    val enableDeleteTrip: Boolean = true,

    val okText: String = context.getString(R.string.dk_ok),
    val cancelText: String = context.getString(R.string.dk_cancel),
    val noScoreText: String = context.getString(R.string.dk_trip_detail_no_score),
    val lowAccelText: String = context.getString(R.string.dk_low_accel),
    val weakAccelText: String = context.getString(R.string.dk_weak_accel),
    val goodAccelText: String = context.getString(R.string.dk_good_accel),
    val strongAccelText: String = context.getString(R.string.dk_strong_accel),
    val highAccelText: String = context.getString(R.string.dk_high_accel),
    val goodMaintainText: String = context.getString(R.string.dk_good_maintain),
    val weakMaintainText: String = context.getString(R.string.dk_weak_maintain),
    val badMaintainText: String = context.getString(R.string.dk_bad_maintain),
    val lowDecelText: String = context.getString(R.string.dk_low_decel),
    val weakDecelText: String = context.getString(R.string.dk_weak_decel),
    val goodDecelText: String = context.getString(R.string.dk_good_decel),
    val strongDecelText: String = context.getString(R.string.dk_strong_decel),
    val highDecelText: String = context.getString(R.string.dk_high_decel),
    val ecodrivingGaugeTitle: String = context.getString(R.string.dk_ecodriving_gauge_title),
    val accelerationText: String = context.getString(R.string.dk_safety_accel),
    val decelText: String = context.getString(R.string.dk_safety_decel),
    val adherenceText: String = context.getString(R.string.dk_safety_adherence),
    val safetyGaugeTitle: String = context.getString(R.string.dk_safety_gauge_title),
    val nbUnlockText: String = context.getString(R.string.dk_unlock_number),
    val durationUnlockText: String = context.getString(R.string.dk_unlock_duration),
    val distanceUnlockText: String = context.getString(R.string.dk_unlock_distance),
    val distanceMeterUnit: String = context.getString(R.string.dk_unit_meter),
    val distanceKmUnit: String = context.getString(R.string.dk_unit_km),
    val durationSecondUnit: String = context.getString(R.string.dk_unit_second),
    val durationMinUnit: String = context.getString(R.string.dk_unit_minute),
    val durationHourUnit: String = context.getString(R.string.dk_unit_hour),
    val distractionGaugeTitle: String = context.getString(R.string.dk_distraction_gauge_title),
    val eventDecelText: String = context.getString(R.string.dk_safety_brake),
    val eventAccelText: String = context.getString(R.string.dk_safety_acceleration),
    val lockText: String = context.getString(R.string.dk_lock_event),
    val unlockText: String = context.getString(R.string.dk_unlock_event),
    val endText: String = context.getString(R.string.dk_end_event),
    val startText: String = context.getString(R.string.dk_start_event),
    val eventAccelCritText: String = context.getString(R.string.dk_safety_list_acceleration_critical),
    val eventAdherenceText: String = context.getString(R.string.dk_safety_list_adherence),
    val eventAdherenceCritText: String = context.getString(R.string.dk_safety_list_adherence_critical),
    val eventDecelCritText: String = context.getString(R.string.dk_safety_list_brake_critical),
    val deleteText: String = context.getString(R.string.dk_confirm_delete_trip),
    val failedToDeleteTrip: String = context.getString(R.string.dk_failed_to_delete_trip),
    val tripDeleted: String = context.getString(R.string.dk_trip_deleted)
) : Serializable
