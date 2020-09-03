package com.drivequant.drivekit.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.support.v4.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataUIEntryPoint
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment
import com.drivequant.drivekit.ui.tripdetail.viewmodel.HeaderDay
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.trips.activity.TripsListActivity
import com.drivequant.drivekit.ui.trips.fragment.TripsListFragment
import com.drivequant.drivekit.ui.trips.viewmodel.TripData

object DriverDataUI : DriverDataUIEntryPoint {

    internal var tripData: TripData = TripData.SAFETY
    internal var mapItems: List<MapItem> = listOf(
        MapItem.SAFETY,
        MapItem.ECO_DRIVING,
        MapItem.DISTRACTION,
        MapItem.INTERACTIVE_MAP,
        MapItem.SYNTHESIS
    )

    internal var enableDeleteTrip: Boolean = true
    internal var dayTripDescendingOrder: Boolean = false
    internal var enableAdviceFeedback: Boolean = true
    internal var shouldDisplayVehicleFilter: Boolean = true
    internal var headerDay: HeaderDay = HeaderDay.DISTANCE_DURATION

    internal var mapTraceMainColor: Int = R.color.dkMapTraceMainColor
    internal var mapTraceWarningColor: Int = R.color.dkMapTraceWarningColor
    internal var noTripsRecordedDrawable: Int = R.drawable.dk_no_trips_recorded

    @JvmOverloads
    fun initialize(tripData: TripData = TripData.SAFETY, mapItems: List<MapItem> = listOf(
            MapItem.SAFETY,
            MapItem.ECO_DRIVING,
            MapItem.DISTRACTION,
            MapItem.INTERACTIVE_MAP,
            MapItem.SYNTHESIS)) {
        this.tripData = tripData
        this.mapItems = mapItems
        DriveKitNavigationController.driverDataUIEntryPoint = this
    }

    fun enableAdviceFeedback(enableAdviceFeedback: Boolean) {
        this.enableAdviceFeedback = enableAdviceFeedback
    }

    fun enableDeleteTrip(enableDeleteTrip: Boolean) {
        this.enableDeleteTrip = enableDeleteTrip
    }

    fun dayTripDescendingOrder(dayTripDescendingOrder: Boolean) {
        this.dayTripDescendingOrder = dayTripDescendingOrder
    }

    fun configureHeaderDay(headerDay: HeaderDay) {
        this.headerDay = headerDay
    }

    fun configureVehicleFilter(shouldDisplayVehicleFilter: Boolean) {
        this.shouldDisplayVehicleFilter = shouldDisplayVehicleFilter
    }

    override fun startTripListActivity(context: Context) {
        val intent = Intent(context, TripsListActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun startTripDetailActivity(context: Context, tripId: String) {
        val intent = Intent(context, TripDetailActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun createTripListFragment(): Fragment = TripsListFragment()

    override fun createTripDetailFragment(tripId: String): Fragment =
        TripDetailFragment.newInstance(tripId)
}