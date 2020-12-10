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
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.TripsQueryListener
import com.drivequant.drivekit.driverdata.trip.TripsSyncStatus
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.*
import java.util.*

internal class TripsListViewModel(
    var tripListConfiguration: TripListConfiguration = TripListConfiguration.MOTORIZED()
) : ViewModel() {
    private var trips = listOf<TripsByDate>()
    var filteredTrips = mutableListOf<TripsByDate>()
    var filterItems: MutableList<FilterItem> = mutableListOf()
    val tripsData: MutableLiveData<List<TripsByDate>> = MutableLiveData()
    val filterData: MutableLiveData<List<FilterItem>> = MutableLiveData()
    var syncTripsError: MutableLiveData<Any> = MutableLiveData()
    var vehicleId: String? = null
    var transportationMode: TransportationMode? = null

    fun fetchTrips(synchronizationType: SynchronizationType) {
        if (DriveKitDriverData.isConfigured()) {
            DriveKitDriverData.getTripsOrderByDateDesc(object : TripsQueryListener {
                override fun onResponse(status: TripsSyncStatus, trips: List<Trip>) {
                    if (status == TripsSyncStatus.FAILED_TO_SYNC_TRIPS_CACHE_ONLY) {
                        syncTripsError.postValue(Any())
                    }
                    this@TripsListViewModel.trips = sortTrips(trips)
                    filterTrips(tripListConfiguration)
                }
            }, synchronizationType, TransportationMode.values().asList()) // TODO manage with tripListConfiguration
        } else {
            syncTripsError.postValue(Any())
        }
    }

    fun filterTrips(configuration: TripListConfiguration) {
        tripListConfiguration = configuration
        filteredTrips.clear()
        when (configuration){
            is TripListConfiguration.MOTORIZED -> {
                // TODO vehicleId = configuration.vehicleId
                configuration.vehicleId?.let { vehicleId ->
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
            is TripListConfiguration.ALTERNATIVE -> {
                // TODO transportationModes = configuration.transportationModes
                configuration.transportationMode?.let { transportationMode ->
                    trips.forEach { tripsByDate ->
                        val dayFilteredTrips = tripsByDate.trips.filter { it.transportationMode == transportationMode }
                        if (!dayFilteredTrips.isNullOrEmpty()){
                            filteredTrips.add(TripsByDate(tripsByDate.date, dayFilteredTrips))
                        }
                    }
                }?: run {
                    filteredTrips = trips.toMutableList()
                }
            }
        }
        tripsData.postValue(filteredTrips)
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
            is TripListConfiguration.MOTORIZED -> {
                DriveKitNavigationController.vehicleUIEntryPoint?.getVehiclesFilterItems(context)?.let {
                    filterItems.add(AllTripsVehicleFilterItem())
                    filterItems.addAll(it)
                }
            }
            is TripListConfiguration.ALTERNATIVE -> {
                filterItems.addAll(getTransportationModeFilterItems())
            }
        }
        filterData.postValue(filterItems)
    }

    fun getTripInfo(): DKTripInfo? {
        return when (tripListConfiguration){
            is TripListConfiguration.MOTORIZED -> DriverDataUI.customTripInfo ?: run { AdviceTripInfo() }
            is TripListConfiguration.ALTERNATIVE -> null
        }
    }

    private fun getTransportationModeFilterItems(): List<FilterItem> {
        val transportationModeFilterItems = mutableListOf<FilterItem>()
        transportationModeFilterItems.add(AllTripsTransportationModeFilterItem())
        computeFilterTransportationModes().forEach { mode ->
            val modeFilterItem = object : FilterItem {
                override fun getItemId(): Any? {
                    return mode
                }

                override fun getImage(context: Context): Drawable? {
                    return mode.image(context)
                }

                override fun getTitle(context: Context): String {
                    return mode.text(context)
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
            .asSequence()
            .filter { !it.transportationMode.isAlternative() }
            .filter { it.declaredTransportationMode == null }
            .filter { it.transportationMode != TransportationMode.UNKNOWN }
            .distinctBy { it.transportationMode }
            .map { it.transportationMode }
            .toList()

        val declared = flatTrips
            .asSequence()
            .filter { it.transportationMode.isAlternative() }
            .filter { it.transportationMode != TransportationMode.UNKNOWN }
            .filter { it.declaredTransportationMode != null }
            .distinctBy { it.declaredTransportationMode?.transportationMode }
            .map { it.declaredTransportationMode?.transportationMode }
            .toList()

        transportationModes.addAll(noDeclared)
        declared.forEach {
            it?.let {
                transportationModes.add(it)
            }
        }
        return transportationModes.toSortedSet()
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