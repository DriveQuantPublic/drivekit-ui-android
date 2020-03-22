package com.drivequant.drivekit.vehicle.ui.vehicles.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import com.drivequant.drivekit.vehicle.ui.vehicles.adapter.VehiclesListAdapter
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel
import kotlinx.android.synthetic.main.activity_vehicle_picker.progress_circular
import kotlinx.android.synthetic.main.fragment_vehicles_list.*
import kotlinx.android.synthetic.main.header_vehicle_list.*

class VehiclesListFragment : Fragment() {
    private var firstLaunch = true
    private lateinit var viewModel : VehiclesListViewModel
    private var adapter: VehiclesListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_vehicles_list, container, false).setDKStyle()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(VehiclesListViewModel::class.java)

        viewModel.progressBarObserver.observe(this, Observer {
            it?.let { displayProgressCircular ->
                if (displayProgressCircular){
                    progress_circular.visibility = View.VISIBLE
                } else {
                    hideProgressCircular()
                }
            }
        })

        viewModel.removeBeaconOrBluetoothObserver.observe(this, Observer {
            viewModel.fetchVehicles(SynchronizationType.CACHE)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = DKResource.convertToString(requireContext(), "dk_common_loading")

        refresh_vehicles.setOnRefreshListener {
            updateVehicles()
        }

        val linearLayoutManager = LinearLayoutManager(view.context)
        vehicles_list.layoutManager = linearLayoutManager
        add_vehicle.button()
    }

    override fun onResume() {
        super.onResume()
        if (firstLaunch) {
            updateVehicles()
            firstLaunch = false
        } else {
            viewModel.fetchVehicles(SynchronizationType.CACHE)
        }
        setupAddVehicleButton()
    }

    private fun updateVehicles(){
        adapter?.setTouched(false)
        viewModel.vehiclesData.observe(this, Observer {
            if (viewModel.syncStatus == VehicleSyncStatus.FAILED_TO_SYNC_VEHICLES_CACHE_ONLY){
                Toast.makeText(context, DKResource.convertToString(requireContext(), "dk_vehicle_error_message"), Toast.LENGTH_LONG).show()
            }
            if (it.isNullOrEmpty()){
                linear_layout_header_vehicle_list.visibility = View.VISIBLE
            } else {
                displayVehiclesList()
                if (adapter != null) {
                    adapter?.setVehicles(it)
                    adapter?.notifyDataSetChanged()
                } else {
                    adapter = VehiclesListAdapter(requireContext(), viewModel, it.toMutableList())
                    vehicles_list.adapter = adapter
                }
            }
            activity?.title = viewModel.getScreenTitle(context)
            refresh_vehicles.visibility = View.VISIBLE
            refresh_vehicles.isRefreshing = false
        })
        refresh_vehicles.isRefreshing = true
        viewModel.fetchVehicles()
    }

    private fun setupAddVehicleButton(){
        if (DriveKitVehicleUI.canAddVehicle){
            add_vehicle.visibility = View.VISIBLE
            add_vehicle.setOnClickListener {
                if (viewModel.maxVehiclesReached()) {
                    DKResource.convertToString(requireContext(), "dk_too_many_vehicles_alert")?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                } else {
                    VehiclePickerActivity.launchActivity(requireContext())
                }
            }
        } else {
            add_vehicle.visibility = View.GONE
        }
    }

    private fun displayVehiclesList(){
        linear_layout_header_vehicle_list.visibility = View.GONE
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
}