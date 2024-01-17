package com.drivequant.drivekit.ui.tripdetail.viewmodel

import androidx.fragment.app.Fragment
import com.drivequant.drivekit.core.access.AccessType
import com.drivequant.drivekit.core.access.DriveKitAccess
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.fragments.DriverDistractionFragment
import com.drivequant.drivekit.ui.tripdetail.fragments.EcoDrivingFragment
import com.drivequant.drivekit.ui.tripdetail.fragments.SafetyFragment
import com.drivequant.drivekit.ui.tripdetail.fragments.SpeedingFragment
import com.drivequant.drivekit.ui.tripdetail.fragments.SynthesisFragment
import com.drivequant.drivekit.ui.tripdetail.fragments.TripTimelineFragment

enum class MapItem : DKMapItem {
    ECO_DRIVING,
    SAFETY,
    INTERACTIVE_MAP,
    DISTRACTION,
    SYNTHESIS,
    SPEEDING;

    override fun getImageResource(): Int =
        when (this) {
            ECO_DRIVING -> R.drawable.dk_leaf_tab_icon
            SAFETY -> R.drawable.dk_shield_tab_icon
            INTERACTIVE_MAP -> R.drawable.dk_trip_timeline_tab_icon
            DISTRACTION -> R.drawable.dk_distraction_tab_icon
            SYNTHESIS -> R.drawable.dk_synthesis_tab_icon
            SPEEDING -> R.drawable.dk_speeding_tab_icon
        }


    override fun getAdvice(trip: Trip): TripAdvice? {
        for (advice in trip.tripAdvices) {
            if (this == SAFETY && advice.theme.equals("SAFETY")) {
                return advice
            } else if (this == ECO_DRIVING && advice.theme.equals("ECODRIVING")) {
                return advice
            }
        }
        return null
    }

    override fun getFragment(trip: Trip?, tripDetailViewModel: DKTripDetailViewModel): Fragment? =
        trip?.let {
            when (this) {
                SAFETY -> SafetyFragment.newInstance(it.safety!!)
                ECO_DRIVING -> EcoDrivingFragment.newInstance(it.ecoDriving!!)
                DISTRACTION -> DriverDistractionFragment.newInstance(tripDetailViewModel)
                INTERACTIVE_MAP -> TripTimelineFragment.newInstance(tripDetailViewModel)
                SYNTHESIS -> SynthesisFragment.newInstance(trip)
                SPEEDING -> SpeedingFragment.newInstance(tripDetailViewModel)
            }
        }

    override fun canShowMapItem(trip: Trip): Boolean? = when (this) {
        ECO_DRIVING -> trip.ecoDriving?.let { it.score <= 10 && DriveKitAccess.hasAccess(AccessType.ECODRIVING) }
        SAFETY -> trip.safety?.let { it.safetyScore <= 10 && DriveKitAccess.hasAccess(AccessType.SAFETY) }
        DISTRACTION -> trip.driverDistraction?.let { it.score <= 10 && DriveKitAccess.hasAccess(AccessType.PHONE_DISTRACTION) }
        SPEEDING -> trip.speedingStatistics?.let { it.score <= 10 && DriveKitAccess.hasAccess(AccessType.SPEEDING) }
        INTERACTIVE_MAP, SYNTHESIS -> true
    }

    override fun getAdviceImageResource(): Int? =
        when (this) {
            SAFETY -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_safety_advice
            ECO_DRIVING -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_eco_advice
            INTERACTIVE_MAP, SYNTHESIS, DISTRACTION, SPEEDING -> null
        }

    override fun displayedMarkers(): List<DKMarkerType> =
        when (this) {
            SAFETY -> listOf(DKMarkerType.SAFETY)
            DISTRACTION -> listOf(DKMarkerType.DISTRACTION)
            INTERACTIVE_MAP -> listOf(DKMarkerType.ALL)
            else -> listOf()
        }

    override fun shouldShowDistractionArea(): Boolean =
        this == DISTRACTION || this == INTERACTIVE_MAP

    override fun overrideShortTrip(): Boolean = false

    override fun shouldShowPhoneDistractionArea(): Boolean = this == DISTRACTION

    override fun shouldShowSpeedingArea(): Boolean = this == SPEEDING
}

interface DKMapItem {
    fun getImageResource(): Int
    fun getAdvice(trip: Trip): TripAdvice?
    fun getFragment(trip: Trip?, tripDetailViewModel: DKTripDetailViewModel): Fragment?
    fun canShowMapItem(trip: Trip): Boolean?
    fun displayedMarkers(): List<DKMarkerType>
    fun shouldShowDistractionArea(): Boolean
    fun getAdviceImageResource(): Int?
    fun overrideShortTrip(): Boolean
    fun shouldShowPhoneDistractionArea(): Boolean
    fun shouldShowSpeedingArea() : Boolean
}

enum class DKMarkerType {
    SAFETY, DISTRACTION, ALL
}
