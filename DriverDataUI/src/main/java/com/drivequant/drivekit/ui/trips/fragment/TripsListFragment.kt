package com.drivequant.drivekit.ui.trips.fragment


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.triplist.DKTripList
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.DKHeader
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.common.ui.component.triplist.views.DKTripListView
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.extension.updateSubMenuItemFont
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.toDKTripList
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfiguration
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import com.drivequant.drivekit.ui.trips.viewmodel.TripsListViewModel
import kotlinx.android.synthetic.main.dk_view_content_no_car_trip.text_view_no_car_text
import kotlinx.android.synthetic.main.fragment_trips_list.dk_trips_list_view
import kotlinx.android.synthetic.main.fragment_trips_list.filter_view
import kotlinx.android.synthetic.main.fragment_trips_list.no_car_trips
import kotlinx.android.synthetic.main.fragment_trips_list.no_trips
import kotlinx.android.synthetic.main.fragment_trips_list.progress_circular
import kotlinx.android.synthetic.main.fragment_trips_list.text_view_trips_synthesis
import kotlinx.android.synthetic.main.view_content_no_trips.image_view_no_trips
import kotlinx.android.synthetic.main.view_content_no_trips.no_trips_recorded_text


class TripsListFragment : Fragment() {
    private lateinit var viewModel: TripsListViewModel
    private lateinit var tripsListView : DKTripListView
    private lateinit var tripsList: DKTripList
    private var shouldSyncTrips = true
    private lateinit var synchronizationType: SynchronizationType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_trips_list"
            ), javaClass.simpleName
        )
        activity?.title = context?.getString(R.string.dk_driverdata_trips_list_title)

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(
                this,
                TripsListViewModel.TripsListViewModelFactory(TripListConfiguration.MOTORIZED())
            )[TripsListViewModel::class.java]
        }

        viewModel.shouldShowFilterMenuOption.observe(viewLifecycleOwner) {
            setHasOptionsMenu(it)
        }

        viewModel.tripsData.observe(viewLifecycleOwner) {
            viewModel.getFilterItems(requireContext())
            if (viewModel.filteredTrips.isEmpty()) {
                displayNoTrips()
            } else {
                displayTripsList()
            }
            if (!this::tripsList.isInitialized) {
                tripsList = object : DKTripList {
                    override fun onTripClickListener(itinId: String) {
                        TripDetailActivity.launchActivity(
                            requireActivity(),
                            itinId,
                            tripListConfigurationType = TripListConfigurationType.getType(viewModel.tripListConfiguration),
                            parentFragment = this@TripsListFragment
                        )
                    }

                    override fun getTripData(): TripData = DriverDataUI.tripData
                    override fun getTripsList(): List<DKTripListItem> = it.toDKTripList()
                    override fun getCustomHeader(): DKHeader? = DriverDataUI.customHeader
                    override fun getHeaderDay(): HeaderDay = DriverDataUI.headerDay
                    override fun getDayTripDescendingOrder(): Boolean =
                        DriverDataUI.dayTripDescendingOrder

                    override fun canSwipeToRefresh(): Boolean = true
                    override fun onSwipeToRefresh() {
                        filter_view.spinner.setSelection(0)
                        updateTrips()
                    }
                }
            }
            tripsListView.configure(tripsList)
            if (synchronizationType == SynchronizationType.CACHE && shouldSyncTrips) {
                shouldSyncTrips = false
                updateTrips()
            }
        }

        viewModel.syncTripsError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(
                    context,
                    context?.getString(R.string.dk_driverdata_failed_to_sync_trips),
                    Toast.LENGTH_LONG
                ).show()
            }
            updateProgressVisibility(false)
        }

        synchronizationType = if (viewModel.hasLocalTrips()) {
            SynchronizationType.CACHE
        } else {
            SynchronizationType.DEFAULT
        }
        updateTrips(synchronizationType)
        initFilter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.dk_trip_list_menu_bar, menu)
        context?.let { context ->
            menu.forEach {
                it.icon?.mutate()?.tintDrawable(DriveKitUI.colors.fontColorOnPrimaryColor())
                it.subMenu?.updateSubMenuItemFont(context)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.trips_vehicle -> {
                viewModel.filterTrips(TripListConfiguration.MOTORIZED())
                filter_view.spinner.setSelection(0, false)
                true
            }
            R.id.trips_alternative -> {
                viewModel.filterTrips(TripListConfiguration.ALTERNATIVE())
                filter_view.spinner.setSelection(0, false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initFilter() {
        filter_view.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long) {
                val itemId = viewModel.filterItems[position].getItemId()
                when (viewModel.tripListConfiguration){
                    is TripListConfiguration.MOTORIZED -> {
                        viewModel.filterTrips(TripListConfiguration.MOTORIZED(itemId as String?))
                    }
                    is TripListConfiguration.ALTERNATIVE -> {
                        viewModel.filterTrips(TripListConfiguration.ALTERNATIVE(itemId as TransportationMode?))
                    }
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun configureFilter() {
        if (viewModel.getFilterVisibility()) {
            filter_view.setItems(viewModel.filterItems)
            text_view_trips_synthesis.text = viewModel.getTripSynthesisText(requireContext())
            text_view_trips_synthesis.visibility = View.VISIBLE
            filter_view.visibility = View.VISIBLE
        } else {
            text_view_trips_synthesis.visibility = View.GONE
            filter_view.visibility = View.GONE
        }
    }

    private fun updateTrips(synchronizationType: SynchronizationType = SynchronizationType.DEFAULT) {
        updateProgressVisibility(true)
        viewModel.fetchTrips(synchronizationType)
    }

    private fun displayNoTrips() {
        val view = if (tripsListView.isFilterPlacerHolder()) {
            text_view_no_car_text.text = DKResource.convertToString(requireContext(), "dk_driverdata_no_trip_placeholder")
            no_car_trips
        } else {
            no_trips
        }
        view.visibility = View.VISIBLE
        text_view_trips_synthesis.visibility = View.GONE
        tripsListView.setTripsListEmptyView(view)
        no_trips_recorded_text.apply {
            text = DKResource.convertToString(requireContext(), "dk_driverdata_no_trips_recorded")
            headLine1()
        }
        image_view_no_trips.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                DriverDataUI.noTripsRecordedDrawable
            )
        )
        updateProgressVisibility(false)
    }

    private fun displayTripsList() {
        configureFilter()
        no_trips.visibility = View.GONE
        dk_trips_list_view.visibility = View.VISIBLE
        updateProgressVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trips_list, container, false)
        tripsListView = view.findViewById(R.id.dk_trips_list_view)
        view.setDKStyle(Color.WHITE)
        return view
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        progress_circular?.apply {
           visibility = if (displayProgress) {
               View.VISIBLE
           } else {
               View.GONE
           }
        }
        tripsListView.updateSwipeRefreshTripsVisibility(displayProgress)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == TripDetailActivity.UPDATE_TRIPS_REQUEST_CODE) {
            updateTrips(SynchronizationType.CACHE)
            filter_view.spinner.setSelection(0, false)
        }
    }
}
