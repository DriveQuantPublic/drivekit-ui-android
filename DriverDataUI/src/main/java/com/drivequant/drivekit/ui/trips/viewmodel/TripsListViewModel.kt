package com.drivequant.drivekit.ui.trips.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.TripsQueryListener
import com.drivequant.drivekit.driverdata.trip.TripsSyncStatus
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.*
import java.util.*

internal class TripsListViewModel(
    private var tripListConfiguration: TripListConfiguration = TripListConfiguration.MOTORIZED
) : ViewModel() {
    private var trips = listOf<TripsByDate>()
    var filteredTrips = mutableListOf<TripsByDate>()
    var filterItems: MutableList<FilterItem> = mutableListOf()
    val tripsData: MutableLiveData<List<TripsByDate>> = MutableLiveData()
    val filterData: MutableLiveData<List<FilterItem>> = MutableLiveData()
    var syncTripsError: MutableLiveData<Any> = MutableLiveData()
    var vehicleId: String? = null

    fun fetchTrips(synchronizationType: SynchronizationType) {
        if (DriveKitDriverData.isConfigured()) {
            DriveKitDriverData.getTripsOrderByDateDesc(object : TripsQueryListener {
                override fun onResponse(status: TripsSyncStatus, trips: List<Trip>) {
                    if (status == TripsSyncStatus.FAILED_TO_SYNC_TRIPS_CACHE_ONLY) {
                        syncTripsError.postValue(Any())
                    }
                    this@TripsListViewModel.trips = sortTrips(trips)
                    filterTrips()
                    tripsData.postValue(filteredTrips)
                }
            }, synchronizationType, TransportationMode.values().asList()) // TODO manage with tripListConfiguration
        } else {
            syncTripsError.postValue(Any())
        }
    }

    fun filterTrips() {
        when (tripListConfiguration){
            TripListConfiguration.MOTORIZED -> {
                vehicleId?.let { vehicleId ->
                    filteredTrips.clear()
                    trips.forEach { tripsByDate ->
                        val dayFilteredTrips = tripsByDate.trips.filter { it.vehicleId == vehicleId }
                        if (!dayFilteredTrips.isNullOrEmpty()){
                            filteredTrips.add(TripsByDate(tripsByDate.date, dayFilteredTrips))
                        }
                    }
                }?: run {
                    filteredTrips = trips.toMutableList()
                }
            }
            TripListConfiguration.ALTERNATIVE -> {
                // TODO
                filteredTrips = trips.toMutableList()

            }
        }
    }

    private fun sortTrips(fetchedTrips: List<Trip>): List<TripsByDate> {
        return fetchedTrips.orderByDay(DriverDataUI.dayTripDescendingOrder)
    }

    fun getTripsByDate(date: Date): TripsByDate? {
        for (currentTripsByDate in filteredTrips) {
            if (currentTripsByDate.date == date) {
                return currentTripsByDate
            }
        }
        return null
    }

    fun getFilterItems(context: Context) {
        filterItems.clear()
        when (tripListConfiguration){
            TripListConfiguration.MOTORIZED -> {
                DriveKitNavigationController.vehicleUIEntryPoint?.getVehiclesFilterItems(context)?.let {
                    filterItems.add(AllTripsVehicleFilterItem())
                    filterItems.addAll(it)
                }
            }
            TripListConfiguration.ALTERNATIVE -> {
                filterItems.add(AllTripsVehicleFilterItem())
                filterItems.addAll(getTransportationModeFilterItems())
            }
        }
        filterData.postValue(filterItems)
    }

    fun changeConfiguration(tripListConfiguration: TripListConfiguration){
        TripsListViewModel@this.tripListConfiguration = tripListConfiguration
    }

    fun getTripInfo(): DKTripInfo? {
        return when (tripListConfiguration){
            TripListConfiguration.MOTORIZED -> DriverDataUI.customTripInfo ?: run { AdviceTripInfo() }
            TripListConfiguration.ALTERNATIVE -> null
        }
    }

    private fun getTransportationModeFilterItems(): List<FilterItem> {
        val transportationModeFilterItems = mutableListOf<FilterItem>()
        computeFilterTransportationModes().forEach { mode ->
            val modeFilterItem = object : FilterItem {
                override fun getItemId(): Any? {
                    return mode
                }

                override fun getImage(context: Context): Drawable? {
                    return DKResource.convertToDrawable(context, mode.image())
                }

                override fun getTitle(context: Context): String {
                    return DKResource.convertToString(context, mode.text())
                }
            }
            transportationModeFilterItems.add(modeFilterItem)
        }
        return transportationModeFilterItems
    }

    private fun computeFilterTransportationModes(): Set<TransportationMode> {
        val flatTrips = trips.flatMap { it.trips }
        val transportationModes = mutableSetOf<TransportationMode>()
        val noDeclared = flatTrips
            .filter { !it.transportationMode.isAlternative() }
            .filter { it.declaredTransportationMode == null }
            .distinctBy { it.transportationMode }.map { it.transportationMode }

        val declared = flatTrips
            .filter { it.transportationMode.isAlternative() }
            .filter { it.declaredTransportationMode != null }
            .distinctBy { it.declaredTransportationMode?.transportationMode }
            .map { it.declaredTransportationMode?.transportationMode }

        transportationModes.addAll(noDeclared)
        declared.forEach {
            it?.let {
                transportationModes.add(it)
            }
        }
        return transportationModes
    }

    fun shouldDisplayAlternativeTrips(): Boolean {
        return getTransportationModeFilterItems().size > 1
    }

    fun getTripSynthesisText(context: Context): SpannableString {
        val flatFilteredTrips = filteredTrips.flatMap { it.trips }
        val tripsNumber = flatFilteredTrips.size
        val tripsDistance = flatFilteredTrips.computeTotalDistance()
        val trip =
            context.resources.getQuantityString(R.plurals.trip_plural, tripsNumber)
        return DKSpannable().append("$tripsNumber", context.resSpans {
            color(DriveKitUI.colors.primaryColor())
            size(R.dimen.dk_text_medium)
            typeface(Typeface.BOLD)
        }).append(" $trip - ", context.resSpans {

        }).append(
            DKDataFormatter.formatDistance(context, tripsDistance),
            context.resSpans {
                color(DriveKitUI.colors.primaryColor())
                size(R.dimen.dk_text_medium)
                typeface(Typeface.BOLD)
            }).toSpannable()
    }

    fun getVehicleFilterVisibility(): Boolean {
        return filterItems.size > 1
    }

    @Suppress("UNCHECKED_CAST")
    class TripsListViewModelFactory(private val tripListConfiguration: TripListConfiguration)
        : ViewModelProvider.NewInstanceFactory() {
        override fun <T: ViewModel?> create(modelClass: Class<T>): T {
            return TripsListViewModel(tripListConfiguration) as T
        }
    }
}