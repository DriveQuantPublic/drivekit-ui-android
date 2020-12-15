package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.support.v4.app.Fragment
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.fragments.AlternativeTripFragment

internal class AlternativeTripMapItem : DKMapItem {
    override fun getImageResource(): Int = R.drawable.dk_alternative_tab_icon

    override fun getAdvice(trip: Trip): TripAdvice? = null

    override fun getFragment(trip: Trip?, tripDetailViewModel: DKTripDetailViewModel): Fragment? {
        return trip?.let {
            AlternativeTripFragment.newInstance(it)
        } ?: run {
            null
        }
    }

    override fun canShowMapItem(trip: Trip): Boolean? = true

    override fun displayedMarkers(): List<DKMarkerType> = listOf()

    override fun shouldShowDistractionArea(): Boolean = false

    override fun getAdviceImageResource(): Int? = null

    override fun overrideShortTrip(): Boolean = true
}