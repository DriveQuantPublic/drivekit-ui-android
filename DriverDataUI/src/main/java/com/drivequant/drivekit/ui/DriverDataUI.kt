package com.drivequant.drivekit.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.support.v4.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataUIEntryPoint
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKHeader
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKMapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.HeaderDay
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.trips.activity.TripsListActivity
import com.drivequant.drivekit.ui.trips.fragment.TripsListFragment
import com.drivequant.drivekit.ui.trips.viewmodel.DKTripInfo
import com.drivequant.drivekit.ui.trips.viewmodel.TripData


object DriverDataUI : DriverDataUIEntryPoint {
    const val TAG = "DriveKit DriverData UI"
    internal var tripData: TripData = TripData.SAFETY
    internal var enableAlternativeTrips: Boolean = false
        private set
    internal var mapItems: List<MapItem> = listOf(
        MapItem.SAFETY,
        MapItem.ECO_DRIVING,
        MapItem.DISTRACTION,
        MapItem.INTERACTIVE_MAP,
        MapItem.SYNTHESIS
    )
        private set

    internal var customMapItem: DKMapItem? = null
        private set
    internal var customHeader: DKHeader? = null
        private set
    internal var customTripInfo: DKTripInfo? = null
        private set

    internal var enableDeleteTrip: Boolean = true
    internal var dayTripDescendingOrder: Boolean = false
    internal var enableAdviceFeedback: Boolean = true
    internal var enableVehicleFilter: Boolean = true
    internal var headerDay: HeaderDay = HeaderDay.DURATION_DISTANCE

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

        checkGoogleApiKey()
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

    fun enableVehicleFilter(enableVehicleFilter: Boolean) {
        this.enableVehicleFilter = enableVehicleFilter
    }

    fun enableAlternativeTrips(enableAlternativeTrips: Boolean) {
        this.enableAlternativeTrips = enableAlternativeTrips
    }

    fun setCustomMapScreen(customMapItem: DKMapItem?) {
        this.customMapItem = customMapItem
    }

    fun customizeHeader(header: DKHeader?) {
        this.customHeader = header
    }

    fun setCustomTripInfo(tripInfo: DKTripInfo?) {
        this.customTripInfo = tripInfo
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

    private fun checkGoogleApiKey(){
        DriveKit.applicationContext?.also { context ->
            try {
                val bundle= context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA).metaData
                val apiKey = bundle.getString("com.google.android.geo.API_KEY")
                if (apiKey.isNullOrBlank()) {
                    DriveKitLog.e(TAG, "A Google API key must be provided in your AndroidManifest.xml. Please refer to the DriveKit documentation.")
                }
            } catch (e: Exception){
                e.localizedMessage?.let {
                    DriveKitLog.e(TAG, "Error while checking the Google API key : $it" )
                }
            }
        }
    }
}