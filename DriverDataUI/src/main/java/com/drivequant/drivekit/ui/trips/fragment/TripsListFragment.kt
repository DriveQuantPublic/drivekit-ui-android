package com.drivequant.drivekit.ui.trips.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.adapter.TripsListAdapter
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfiguration
import com.drivequant.drivekit.ui.trips.viewmodel.TripsListViewModel
import kotlinx.android.synthetic.main.dk_view_content_no_car_trip.*
import kotlinx.android.synthetic.main.fragment_trips_list.*
import kotlinx.android.synthetic.main.view_content_no_trips.*

class TripsListFragment : Fragment() {
    private lateinit var viewModel: TripsListViewModel
    private var adapter: TripsListAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        progress_circular.visibility = View.VISIBLE
        // TODO
        viewModel = ViewModelProviders.of(this,
            TripsListViewModel.TripsListViewModelFactory(TripListConfiguration.MOTORIZED()))
            .get(TripsListViewModel::class.java)

        viewModel.filterData.observe(this, Observer {
            configureFilter()
        })

        initFilter()
        updateTrips()
        viewModel.tripsData.observe(this, Observer {
            viewModel.getFilterItems(requireContext())
            setHasOptionsMenu(DriverDataUI.enableAlternativeTrips && viewModel.computeFilterTransportationModes().isNotEmpty())
            if (viewModel.filteredTrips.isEmpty()) {
                displayNoTrips()
                adapter?.notifyDataSetChanged()
            } else {
                displayTripsList()
                adapter?.notifyDataSetChanged() ?: run {
                    adapter = TripsListAdapter(view?.context, viewModel)
                    trips_list.setAdapter(adapter)
                }
            }
            updateProgressVisibility(false)
        })

        viewModel.syncTripsError.observe(this, Observer {
            it?.let {
                Toast.makeText(
                    context,
                    context?.getString(R.string.dk_driverdata_failed_to_sync_trips),
                    Toast.LENGTH_LONG
                ).show()
            }
            updateProgressVisibility(false)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.trip_list_menu_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.trips_vehicle -> {
                viewModel.filterTrips(TripListConfiguration.MOTORIZED())
                true
            }
            R.id.trips_alternative -> {
                viewModel.filterTrips(TripListConfiguration.ALTERNATIVE())
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_trips_list"
            ), javaClass.simpleName
        )
        activity?.title = context?.getString(R.string.dk_driverdata_trips_list_title)
        refresh_trips.setOnRefreshListener {
            filter_view.spinner.setSelection(0)
            updateTrips()
        }
        trips_list.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            adapter?.getChild(groupPosition, childPosition)?.let {
                TripDetailActivity.launchActivity(requireActivity(), it.itinId)
            }
            false
        }
    }

    fun updateTrips(synchronizationType: SynchronizationType = SynchronizationType.DEFAULT) {
        updateProgressVisibility(true)
        viewModel.fetchTrips(synchronizationType)
    }

    private fun displayNoTrips() {
        val view = if (adapter != null) {
            text_view_no_car_text.text = DKResource.convertToString(requireContext(), "dk_driverdata_no_trip_placeholder")
            no_car_trips
        } else {
            no_trips
        }
        view.visibility = View.VISIBLE
        text_view_trips_synthesis.visibility = View.GONE
        trips_list.emptyView = view
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
        hideProgressCircular()
    }

    private fun displayTripsList() {
        no_trips.visibility = View.GONE
        trips_list.visibility = View.VISIBLE
        hideProgressCircular()
    }

    private fun hideProgressCircular() {
        progress_circular.animate()
            .alpha(0f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progress_circular?.visibility = View.GONE
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trips_list, container, false)
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        return view
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
            refresh_trips.isRefreshing = true
        } else {
            progress_circular.visibility = View.GONE
            refresh_trips.visibility = View.VISIBLE
            refresh_trips.isRefreshing = false
        }
    }
}