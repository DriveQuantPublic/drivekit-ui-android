package com.drivequant.drivekit.ui.trips.fragment

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
import com.drivequant.drivekit.driverdata.trip.TripsSyncStatus
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.TripsViewConfig
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.adapter.TripsListAdapter
import com.drivequant.drivekit.ui.trips.viewmodel.TripsListViewModel
import kotlinx.android.synthetic.main.fragment_trips_list.*
import kotlinx.android.synthetic.main.view_content_no_trips.*

class TripsListFragment : Fragment() {
    private lateinit var viewModel : TripsListViewModel
    private lateinit var tripsViewConfig : TripsViewConfig
    private lateinit var tripDetailViewConfig : TripDetailViewConfig
    private var adapter: TripsListAdapter? = null

    companion object {
        fun newInstance(tripsViewConfig: TripsViewConfig, tripDetailViewConfig: TripDetailViewConfig) : TripsListFragment {
            val fragment = TripsListFragment()
            fragment.tripsViewConfig = tripsViewConfig
            fragment.tripDetailViewConfig = tripDetailViewConfig
            return fragment
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        progress_circular.visibility = View.VISIBLE
        viewModel = ViewModelProviders.of(this).get(TripsListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (savedInstanceState?.getSerializable("config") as TripsViewConfig?)?.let{
            tripsViewConfig = it
        }
        (savedInstanceState?.getSerializable("detailConfig") as TripDetailViewConfig?)?.let{
            tripDetailViewConfig = it
        }
        activity?.title = tripsViewConfig.viewTitleText
        refresh_trips.setOnRefreshListener {
            updateTrips()
        }
        trips_list.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            adapter?.getChild(groupPosition, childPosition)?.let{
                TripDetailActivity.launchActivity(requireContext(), it.itinId, tripsViewConfig = tripsViewConfig)
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        updateTrips()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("config", tripsViewConfig)
        outState.putSerializable("detailConfig", tripDetailViewConfig)
        super.onSaveInstanceState(outState)
    }

    private fun updateTrips(){
        viewModel.tripsData.observe(this, Observer {
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
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trips_list, container, false)
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