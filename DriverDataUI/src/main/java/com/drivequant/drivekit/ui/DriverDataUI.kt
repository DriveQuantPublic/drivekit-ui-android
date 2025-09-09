package com.drivequant.drivekit.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.component.lasttripscards.DKLastTripsUI
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.DKHeader
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverDataUIEntryPoint
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.utils.getApplicationInfoCompat
import com.drivequant.drivekit.ui.driverprofile.DriverProfileActivity
import com.drivequant.drivekit.ui.drivingconditions.DrivingConditionsActivity
import com.drivequant.drivekit.ui.drivingconditions.component.context.DKContextKind
import com.drivequant.drivekit.ui.extension.toDKTripList
import com.drivequant.drivekit.ui.lasttripscards.LastTripsWidgetUtils
import com.drivequant.drivekit.ui.mysynthesis.MySynthesisActivity
import com.drivequant.drivekit.ui.synthesiscards.LastTripsSynthesisCard
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity.Companion.ITINID_EXTRA
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity.Companion.OPEN_ADVICE_EXTRA
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKMapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.trips.activity.TripsListActivity
import com.drivequant.drivekit.ui.trips.fragment.TripsListFragment
import com.drivequant.drivekit.ui.trips.viewmodel.DKTripInfo

object DriverDataUI : DriverDataUIEntryPoint {
    internal const val TAG = "DriveKit Driver Data UI"

    var contextKinds: List<DKContextKind> = DKContextKind.values().toList()
        private set
    var tripData: TripData = TripData.SAFETY
        private set
    var enableAlternativeTrips: Boolean = false
        private set
    var enableOccupantDeclaration: Boolean = false
        private set
    internal var mapItems: List<MapItem> = listOf(
        MapItem.SAFETY,
        MapItem.ECO_DRIVING,
        MapItem.DISTRACTION,
        MapItem.SPEEDING,
        MapItem.INTERACTIVE_MAP,
        MapItem.SYNTHESIS
    )
        private set

    @JvmStatic
    var customMapItem: DKMapItem? = null
        private set
    internal var customHeader: DKHeader? = null
        private set
    @JvmStatic
    var customTripInfo: DKTripInfo? = null
    internal var enableDeleteTrip: Boolean = true
    internal var dayTripDescendingOrder: Boolean = false
    internal var enableAdviceFeedback: Boolean = true
    internal var enableVehicleFilter: Boolean = true
    internal var headerDay: HeaderDay = HeaderDay.DURATION_DISTANCE

    @JvmStatic
    var alternativeTripsDepthInDays: Int? = null

    @ColorRes
    internal var mapTraceMainColor: Int = R.color.dkMapTraceMainColor
    @ColorRes
    internal var mapTraceWarningColor: Int = R.color.dkMapTraceWarningColor
    @ColorRes
    internal var mapTraceAuthorizedCallColor: Int = R.color.dkMapTraceAuthorizedCallColor

    init {
        DriveKit.checkInitialization()
        DriveKitLog.i(TAG, "Initialization")
        DriveKitNavigationController.driverDataUIEntryPoint = this
        checkGoogleApiKey()
    }

    @JvmStatic
    fun initialize() {
        // Nothing to do currently.
    }

    @JvmStatic
    fun enableAdviceFeedback(enableAdviceFeedback: Boolean) {
        this.enableAdviceFeedback = enableAdviceFeedback
    }

    @JvmStatic
    fun enableDeleteTrip(enableDeleteTrip: Boolean) {
        this.enableDeleteTrip = enableDeleteTrip
    }

    @JvmStatic
    fun dayTripDescendingOrder(dayTripDescendingOrder: Boolean) {
        this.dayTripDescendingOrder = dayTripDescendingOrder
    }

    @JvmStatic
    fun configureTripData(tripData: TripData) {
        this.tripData = tripData
    }

    @JvmStatic
    fun configureMapItems(mapItems: List<MapItem>) {
        this.mapItems = mapItems
    }

    @JvmStatic
    fun configureHeaderDay(headerDay: HeaderDay) {
        this.headerDay = headerDay
    }

    @JvmStatic
    fun configureContextKinds(contextKinds: List<DKContextKind>) {
        this.contextKinds = contextKinds
    }

    @JvmStatic
    fun enableVehicleFilter(enableVehicleFilter: Boolean) {
        this.enableVehicleFilter = enableVehicleFilter
    }

    @JvmStatic
    fun enableAlternativeTrips(enableAlternativeTrips: Boolean) {
        this.enableAlternativeTrips = enableAlternativeTrips
    }

    @JvmStatic
    fun enableOccupantDeclaration(enableOccupantDeclaration: Boolean) {
        this.enableOccupantDeclaration = enableOccupantDeclaration
    }

    @JvmStatic
    fun setCustomMapScreen(customMapItem: DKMapItem?) {
        this.customMapItem = customMapItem
    }

    @JvmStatic
    fun customizeHeader(header: DKHeader?) {
        this.customHeader = header
    }

    override fun startTripListActivity(context: Context) {
        val intent = Intent(context, TripsListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun startTripDetailActivity(context: Context, tripId: String, openAdvice: Boolean) {
        val intent = Intent(context, TripDetailActivity::class.java)
        intent.putExtra(ITINID_EXTRA, tripId)
        intent.putExtra(OPEN_ADVICE_EXTRA, openAdvice)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun createTripListFragment(): Fragment = TripsListFragment()

    override fun createTripDetailFragment(tripId: String): Fragment =
        TripDetailFragment.newInstance(tripId)

    override fun startMySynthesisActivity(context: Context) = MySynthesisActivity.launchActivity(context)
    override fun startDrivingConditionsActivity(context: Context) = DrivingConditionsActivity.launchActivity(context)
    override fun startDriverProfileActivity(context: Context) = DriverProfileActivity.launchActivity(context)

    private fun checkGoogleApiKey() {
        try {
            val bundle = DriveKit.applicationContext.packageManager.getApplicationInfoCompat(DriveKit.applicationContext.packageName, PackageManager.GET_META_DATA).metaData
            val apiKey = bundle.getString("com.google.android.geo.API_KEY")
            if (apiKey.isNullOrBlank()) {
                DriveKitLog.e(TAG, "A Google API key must be provided in your AndroidManifest.xml. Please refer to the DriveKit documentation.")
            }
        } catch (e: Exception) {
            e.localizedMessage?.let {
                DriveKitLog.e(TAG, "Error while checking the Google API key : $it" )
            }
        }
    }

    @JvmOverloads
    @JvmStatic
    fun getLastTripsSynthesisCardsView(
        synthesisCards: List<LastTripsSynthesisCard> = listOf(
            LastTripsSynthesisCard.SAFETY,
            LastTripsSynthesisCard.ECO_DRIVING,
            LastTripsSynthesisCard.DISTRACTION,
            LastTripsSynthesisCard.SPEEDING
        ), listener: SynthesisCardsViewListener
    ) {
        DriverDataSynthesisCardsUI.getLastTripsSynthesisCardsView(synthesisCards, listener)
    }

    @JvmOverloads
    @JvmStatic
    fun getLastTripsView(
        headerDay: HeaderDay = HeaderDay.DISTANCE,
        lastTripMaxNumber: Int = 10
    ): Fragment {
        val trips = LastTripsWidgetUtils.getLastTrips(lastTripMaxNumber)
        return DKLastTripsUI.getLastTripWidget(trips.toDKTripList(), headerDay, tripData)
    }
}
