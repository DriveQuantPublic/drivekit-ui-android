package com.drivequant.drivekit.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.support.v4.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataUIEntryPoint
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.trips.activity.TripsListActivity
import com.drivequant.drivekit.ui.trips.fragment.TripsListFragment
import com.drivequant.drivekit.ui.trips.viewmodel.TripData

object DriverDataUI : DriverDataUIEntryPoint {

    lateinit var tripData: TripData
    var enableDeleteTrip: Boolean = false
    var dayTripDescendingOrder: Boolean = false
    var displayAdvices: Boolean = false
    var enableAdviceFeedback: Boolean = false
    var mapItems: List<MapItem> = listOf()

    fun initialize(
         tripData: TripData = TripData.SAFETY,
         enableDeleteTrip: Boolean = true,
         dayTripDescendingOrder: Boolean = true,
         displayAdvices: Boolean = true,
         enableAdviceFeedback: Boolean = false,
         mapItems :List<MapItem> = listOf(
             MapItem.SAFETY,
             MapItem.ECO_DRIVING,
             MapItem.DISTRACTION,
             MapItem.INTERACTIVE_MAP,
             MapItem.SYNTHESIS)) {

        this.tripData = tripData
        this.enableDeleteTrip = enableDeleteTrip
        this.dayTripDescendingOrder = dayTripDescendingOrder
        this.displayAdvices = displayAdvices
        this.enableAdviceFeedback = enableAdviceFeedback
        this.mapItems = mapItems
        DriveKitNavigationController.driverDataUIEntryPoint = this
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

    override fun createTripDetailFragment(tripId: String): Fragment = TripDetailFragment.newInstance(tripId)

    var mapTraceMainColor: Int = R.color.dkMapTraceMainColor
    var mapTraceWarningColor: Int = R.color.dkMapTraceWarningColor
    var noTripsRecordedDrawable: Int = R.drawable.dk_no_trips_recorded
}