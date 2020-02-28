package com.drivequant.drivekit.ui

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataUIEntryPoint
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.trips.fragment.TripsListFragment
import com.drivequant.drivekit.ui.trips.viewmodel.TripData

object DriverDataUI : DriverDataUIEntryPoint {

    fun initialize() {
        DriveKitNavigationController.driverDataUIEntryPoint = this
    }

    override fun startTripListActivity(context: Context) {

    }

    override fun startTripDetailActivity(context: Context, tripId: String) =
        context.startActivity(Intent(context, TripDetailActivity::class.java))

    override fun createTripListFragment(): Fragment = TripsListFragment()

    override fun createTripDetailFragment(tripId: String): Fragment = TripDetailFragment.newInstance(tripId)

    var mapItems: List<MapItem> = listOf(
        MapItem.SAFETY,
        MapItem.ECO_DRIVING,
        MapItem.DISTRACTION,
        MapItem.INTERACTIVE_MAP,
        MapItem.SYNTHESIS
    )
    var displayAdvices: Boolean = true
    var mapTraceMainColor: Int = R.color.dkMapTraceMainColor
    var mapTraceWarningColor: Int = R.color.dkMapTraceWarningColor
    var enableDeleteTrip: Boolean = true
    var tripData: TripData = TripData.SAFETY
    var dayTripDescendingOrder: Boolean = true
    var noTripsRecordedDrawable: Int = R.drawable.dk_no_trips_recorded
    var enableAdviceFeedback: Boolean = false
}