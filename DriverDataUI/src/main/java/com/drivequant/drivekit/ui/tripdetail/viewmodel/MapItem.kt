package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.support.v4.app.Fragment
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.fragments.*

enum class MapItem : DKMapItem {
    ECO_DRIVING,
    SAFETY,
    INTERACTIVE_MAP,
    DISTRACTION,
    SYNTHESIS;

    override fun getImageResource(): Int =
        when (this) {
            ECO_DRIVING -> R.drawable.dk_leaf_tab_icon
            SAFETY -> R.drawable.dk_shield_tab_icon
            INTERACTIVE_MAP -> R.drawable.dk_trip_timeline_tab_icon
            DISTRACTION -> R.drawable.dk_distraction_tab_icon
            SYNTHESIS -> R.drawable.dk_synthesis_tab_icon
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

    override fun getFragment(trip: Trip, tripDetailViewModel: DKTripDetailViewModel): Fragment =
        when (this) {
            SAFETY -> SafetyFragment.newInstance(trip.safety!!)
            ECO_DRIVING -> EcoDrivingFragment.newInstance(trip.ecoDriving!!)
            DISTRACTION -> DriverDistractionFragment.newInstance(trip.driverDistraction!!)
            INTERACTIVE_MAP -> TripTimelineFragment.newInstance(tripDetailViewModel)
            SYNTHESIS -> SynthesisFragment.newInstance(trip)
        }

    override fun canShowMapItem(trip: Trip): Boolean =
        when (this) {
            ECO_DRIVING -> trip.ecoDriving?.let { it.score <= 10 }!!
            SAFETY -> trip.safety?.let { it.safetyScore <= 10 }!!
            INTERACTIVE_MAP, SYNTHESIS -> true
            DISTRACTION -> trip.driverDistraction?.let { it.score <= 10 }!!
        }

    override fun getAdviceImageResource(): Int? =
        when (this) {
            SAFETY -> R.drawable.dk_safety_advice
            ECO_DRIVING -> R.drawable.dk_eco_advice
            INTERACTIVE_MAP, SYNTHESIS, DISTRACTION -> null
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
}

interface DKMapItem {
    fun getImageResource(): Int
    fun getAdvice(trip: Trip): TripAdvice?
    fun getFragment(trip: Trip, tripDetailViewModel: DKTripDetailViewModel): Fragment
    fun canShowMapItem(trip: Trip): Boolean
    fun displayedMarkers(): List<DKMarkerType>
    fun shouldShowDistractionArea(): Boolean
    fun getAdviceImageResource(): Int?
    fun overrideShortTrip(): Boolean
}