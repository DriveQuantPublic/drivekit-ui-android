package com.drivequant.drivekit.vehicle.ui.manager.viewmodel.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.vehicle.ui.manager.viewmodel.VehiclesListViewModel
import com.drivequant.drivekit.vehicle.ui.manager.viewmodel.adapter.VehiclesListAdapter
import kotlinx.android.synthetic.main.activity_vehicle_picker.*

class VehiclesListFragment : Fragment() {
    private lateinit var viewModel : VehiclesListViewModel
    private var adapter: VehiclesListAdapter? = null

    companion object {
        fun newInstance() : VehiclesListFragment {
            return VehiclesListFragment()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        progress_circular.visibility = View.VISIBLE
        viewModel = ViewModelProviders.of(this).get(VehiclesListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "HC mes véhicules"
        refresh_trips.setOnRefreshListener {
            updateVehicles()
        }
    }

    override fun onResume() {
        super.onResume()
        updateVehicles()
    }

    private fun updateVehicles(){
        viewModel.vehiclesData.observe(this, Observer {
            if (viewModel.syncStatus != TripsSyncStatus.NO_ERROR){
                Toast.makeText(context, tripsViewConfig.failedToSyncTrips, Toast.LENGTH_LONG).show()
            }
            if (viewModel.tripsByDate.isNullOrEmpty()){
                displayNoTrips()
            } else {
                displayTripsList()
                if (adapter != null) {
                    adapter?.notifyDataSetChanged()
                } else {
                    adapter = TripsListAdapter(view?.context, viewModel, tripsViewConfig)
                    trips_list.setAdapter(adapter)
                }
            }
            updateProgressVisibility(false)
        })
        updateProgressVisibility(true)
        viewModel.fetchTrips(tripsViewConfig.dayTripDescendingOrder)
    }

    private fun displayNoTrips(){
        no_trips.visibility = View.VISIBLE
        trips_list.emptyView = no_trips
        no_trips_recorded_text.text = tripsViewConfig.noTripsRecordedText
        no_trips_recorded_text.setTextColor(tripsViewConfig.primaryColor)
        image_view_no_trips.setImageDrawable(ContextCompat.getDrawable(requireContext(), tripsViewConfig.noTripsRecordedDrawable))
        hideProgressCircular()
    }

    private fun displayTripsList(){
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
        savedInstanceState: Bundlefragment_vehicles_list?
    ): View? {
        return inflater.inflate(R.layout.fragment_vehicles_list, container, false)
    }

    fun updateProgressVisibility(displayProgress: Boolean){
        if (displayProgress){
            progress_circular.visibility = View.VISIBLE
            refresh_trips.isRefreshing = true
        } else {
            progress_circular.visibility = View.GONE
            refresh_trips.visibility = View.VISIBLE
            refresh_trips.isRefreshing = false
        }
    }
}