package com.drivequant.drivekit.ui.trips.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.driverdata.trip.TripsSyncStatus
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.adapter.TripsListAdapter
import com.drivequant.drivekit.ui.trips.viewmodel.TripsListViewModel
import kotlinx.android.synthetic.main.fragment_trips_list.*
import kotlinx.android.synthetic.main.view_content_no_trips.*

class TripsListFragment : Fragment() {
    private lateinit var viewModel: TripsListViewModel
    private var adapter: TripsListAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        progress_circular.visibility = View.VISIBLE
        viewModel = ViewModelProviders.of(this).get(TripsListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = context?.getString(R.string.dk_driverdata_trips_list_title)
        refresh_trips.setOnRefreshListener {
            filter_view_vehicle.spinner.setSelection(0)
            updateTrips()
        }
        trips_list.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            adapter?.getChild(groupPosition, childPosition)?.let {
                TripDetailActivity.launchActivity(requireContext(), it.itinId)
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        setupVehicleFilterView()
    }

    private fun vehicleFilterVisibility(visibility: Boolean) {
        text_view_trips_synthesis.visibility =  if (visibility) View.VISIBLE else View.GONE
        filter_view_vehicle.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    private fun setupVehicleFilterView() {
        viewModel.filterData.observe(this, Observer {
            filter_view_vehicle.setItems(viewModel.filterItems)
            vehicleFilterVisibility(true)
        })
        viewModel.getVehiclesFilterItems(requireContext())
        filter_view_vehicle.spinner.onItemSelectedListener = object :
            OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, l: Long) {
                viewModel.currentItemPosition = position
                updateTrips()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun updateTripsSynthesis() {
        if (viewModel.tripsByDate.isEmpty()) {
            text_view_trips_synthesis.visibility = View.GONE
        } else {
            val tripsNumber = viewModel.computedSynthesis.first
            val tripsDistance = viewModel.computedSynthesis.second
            val trip =
                requireContext().resources.getQuantityString(R.plurals.trip_plural, tripsNumber)
            val tripSynthesis = DKSpannable().append("$tripsNumber", requireContext().resSpans {
                color(DriveKitUI.colors.primaryColor())
                size(R.dimen.dk_text_medium)
                typeface(Typeface.BOLD)
            }).append(" - $trip ", requireContext().resSpans {

            }).append(
                DKDataFormatter.formatDistance(requireContext(), tripsDistance),
                requireContext().resSpans {
                    color(DriveKitUI.colors.primaryColor())
                    size(R.dimen.dk_text_medium)
                    typeface(Typeface.BOLD)
                }).toSpannable()
            text_view_trips_synthesis.text = tripSynthesis
        }
    }

    private fun updateTrips() {
        viewModel.tripsData.observe(this, Observer {
            if (viewModel.syncStatus != TripsSyncStatus.NO_ERROR) {
                Toast.makeText(
                    context,
                    context?.getString(R.string.dk_driverdata_failed_to_sync_trips),
                    Toast.LENGTH_LONG
                ).show()
            }
            if (viewModel.tripsByDate.isNullOrEmpty()) {
                text_view_trips_synthesis.visibility = View.GONE
                displayNoTrips()
            } else {
                if (DriverDataUI.shouldDisplayVehicleFilter) {
                    updateTripsSynthesis()
                }
                displayTripsList()
                if (adapter != null) {
                    adapter?.notifyDataSetChanged()
                } else {
                    adapter = TripsListAdapter(view?.context, viewModel)
                    trips_list.setAdapter(adapter)
                }
            }
            updateProgressVisibility(false)
        })
        updateProgressVisibility(true)
        viewModel.fetchTrips(DriverDataUI.dayTripDescendingOrder)
    }

    private fun displayNoTrips() {
        no_trips.visibility = View.VISIBLE
        trips_list.emptyView = no_trips
        no_trips_recorded_text.text = context?.getString(R.string.dk_driverdata_no_trips_recorded)
        no_trips_recorded_text.setTextColor(DriveKitUI.colors.primaryColor())
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