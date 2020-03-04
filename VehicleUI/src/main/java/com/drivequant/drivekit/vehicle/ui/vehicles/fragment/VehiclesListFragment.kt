package com.drivequant.drivekit.vehicle.ui.vehicles.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel
import com.drivequant.drivekit.vehicle.ui.vehicles.adapter.VehiclesListAdapter
import kotlinx.android.synthetic.main.activity_vehicle_picker.progress_circular
import kotlinx.android.synthetic.main.fragment_vehicles_list.*

class VehiclesListFragment : Fragment() {
    private lateinit var viewModel : VehiclesListViewModel
    private lateinit var status: VehicleSyncStatus
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
        activity?.title = "HC Chargement…" // TODO
        refresh_vehicles.setOnRefreshListener {
            updateVehicles()
        }
    }

    override fun onResume() {
        super.onResume()
        updateVehicles()
    }

    private fun updateVehicles(){
        viewModel.vehiclesData.observe(this, Observer {
            if (viewModel.syncStatus != VehicleSyncStatus.NO_ERROR){
                Toast.makeText(context, "HC failed to sync vehicles", Toast.LENGTH_LONG).show() // TODO
            }
            if (it.isNullOrEmpty()){
                displayNoVehicle()
            } else {
                displayVehiclesList()
                if (adapter != null) {
                    adapter?.notifyDataSetChanged()
                } else {
                    adapter = VehiclesListAdapter(context, viewModel, it)
                    vehicles_list.adapter = adapter
                }
            }
            activity?.title = viewModel.getScreenTitle(context)
            updateProgressVisibility(false)
        })
        updateProgressVisibility(true)
        viewModel.fetchVehicles()
    }
    private fun displayNoVehicle(){
        no_vehicles.visibility = View.VISIBLE
        vehicles_list.emptyView = no_vehicles
        hideProgressCircular()
    }

    private fun displayVehiclesList(){
        no_vehicles.visibility = View.GONE
        vehicles_list.visibility = View.VISIBLE
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
        return inflater.inflate(R.layout.fragment_vehicles_list, container, false)
    }

    fun updateProgressVisibility(displayProgress: Boolean){
        if (displayProgress){
            progress_circular.visibility = View.VISIBLE
            refresh_vehicles.isRefreshing = true
        } else {
            progress_circular.visibility = View.GONE
            refresh_vehicles.visibility = View.VISIBLE
            refresh_vehicles.isRefreshing = false
        }
    }
}