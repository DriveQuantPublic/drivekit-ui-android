package com.drivekit.demoapp.driverdata

import android.support.v4.app.Fragment
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKMapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKMarkerType
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKTripDetailViewModel

class DemoCustomMapItem : DKMapItem {
    override fun getAdvice(trip: Trip): TripAdvice? = null
    override fun getFragment(trip: Trip?, tripDetailViewModel: DKTripDetailViewModel): Fragment = Fragment()
    override fun displayedMarkers(): List<DKMarkerType> = listOf(DKMarkerType.SAFETY)
    override fun canShowMapItem(trip: Trip): Boolean? = true
    override fun getImageResource(): Int = R.drawable.dk_leaf_tab_icon
    override fun getAdviceImageResource(): Int? = null
    override fun shouldShowDistractionArea(): Boolean = false
    override fun overrideShortTrip(): Boolean = false
}