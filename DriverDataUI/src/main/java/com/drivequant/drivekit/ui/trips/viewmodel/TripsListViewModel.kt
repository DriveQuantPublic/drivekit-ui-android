package com.drivequant.drivekit.ui.trips.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.DistanceUnit
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
        private set
    var filterItems: MutableList<FilterItem> = mutableListOf()
        private set
    val tripsData: MutableLiveData<List<TripsByDate>> = MutableLiveData()
    val filterData: MutableLiveData<List<FilterItem>> = MutableLiveData()
    var syncTripsError: MutableLiveData<Any> = MutableLiveData()
        private set

    fun fetchTrips(synchronizationType: SynchronizationType) {
        if (DriveKitDriverData.isConfigured()) {
            var handler: Handler? = null
            Looper.myLooper()?.let {
                handler = Handler(it)
            }
            handler?.post {
                val transportationModes: MutableList<TransportationMode> = TripListConfiguration.MOTORIZED().getTransportationModes().toMutableList()
                if (DriverDataUI.enableAlternativeTrips){
                    transportationModes.addAll(TripListConfiguration.ALTERNATIVE().getTransportationModes())
                }
                DriveKitDriverData.getTripsOrderByDateDesc(object : TripsQueryListener {
                    override fun onResponse(status: TripsSyncStatus, trips: List<Trip>) {
                        if (status == TripsSyncStatus.FAILED_TO_SYNC_TRIPS_CACHE_ONLY) {
                            syncTripsError.postValue(Any())
                        }
                        this@TripsListViewModel.trips = sortTrips(trips)
                        filterTrips(tripListConfiguration)
                    }
                }, synchronizationType, transportationModes)
            }
        } else {
            syncTripsError.postValue(Any())
        }
    }

    fun filterTrips(configuration: TripListConfiguration) {
        tripListConfiguration = configuration
        filteredTrips.clear()
        when (configuration){
            is TripListConfiguration.MOTORIZED -> {
                trips.forEach { tripsByDate ->
                    val dayFilteredTrips = tripsByDate.trips.filter { configuration.vehicleId?.let { vehicleId -> it.vehicleId == vehicleId }?:run { !it.transportationMode.isAlternative() } }
                    if (!dayFilteredTrips.isNullOrEmpty()){
                        filteredTrips.add(TripsByDate(tripsByDate.date, dayFilteredTrips))
                    }
                }
            }
            is TripListConfiguration.ALTERNATIVE -> {
                trips.forEach { tripsByDate ->
                    val mode = configuration.transportationMode
                    val dayFilteredTrips = if (mode == null){
                        tripsByDate.trips.filter { it.transportationMode.isAlternative() }
                    } else {
                        if (!configuration.transportationMode.isAlternative()){
                            tripsByDate.trips.filter { it.declaredTransportationMode?.transportationMode == mode }
                        } else {
                            tripsByDate.trips.filter { (it.transportationMode == mode && it.declaredTransportationMode == null) || it.declaredTransportationMode?.transportationMode == mode }
                        }
                    }
                    if (!dayFilteredTrips.isNullOrEmpty()){
                        filteredTrips.add(TripsByDate(tripsByDate.date, dayFilteredTrips))
                    }
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
                override fun getItemId(): Any {
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

    fun computeFilterTransportationModes(): Set<TransportationMode> {
        val transportationModes = mutableSetOf<TransportationMode>()
        val flatTrips = trips.flatMap { it.trips }
        TripListConfiguration.MOTORIZED().getTransportationModes().forEach {
            if (DriveKitDriverData.tripsQuery()
                    .whereEqualTo("DeclaredTransportationMode_transportationMode", it.value)
                    .query().execute().isNotEmpty()
            ) {
                transportationModes.add(it)
            }
        }

        TripListConfiguration.ALTERNATIVE().getTransportationModes().forEach { mode ->
            val count = flatTrips.filter { (it.transportationMode == mode && it.declaredTransportationMode == null) || it.declaredTransportationMode?.transportationMode == mode }.size
            if (count > 0) {
                transportationModes.add(mode)
            }
        }
        return transportationModes.toSortedSet()
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
            color(DriveKitUI.colors.complementaryFontColor())
        }).append(
            DKDataFormatter.formatMeterDistanceInKm(context, tripsDistance, false),
            context.resSpans {
                color(DriveKitUI.colors.primaryColor())
                size(R.dimen.dk_text_medium)
                typeface(Typeface.BOLD)
            }
        ).append(" ${DistanceUnit.configuredUnit(context)}", context.resSpans {
            color(DriveKitUI.colors.complementaryFontColor())
        }).toSpannable()
    }

    fun getFilterVisibility(): Boolean {
        return (DriverDataUI.enableVehicleFilter || DriverDataUI.enableAlternativeTrips) && filterItems.size > 1
    }

    @Suppress("UNCHECKED_CAST")
    class TripsListViewModelFactory(private val tripListConfiguration: TripListConfiguration)
        : ViewModelProvider.NewInstanceFactory() {
        override fun <T: ViewModel?> create(modelClass: Class<T>): T {
            return TripsListViewModel(tripListConfiguration) as T
        }
    }
}