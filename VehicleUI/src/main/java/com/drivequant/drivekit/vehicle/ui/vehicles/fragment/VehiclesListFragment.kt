package com.drivequant.drivekit.vehicle.ui.vehicles.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.activity.VehiclePickerActivity
import com.drivequant.drivekit.vehicle.ui.vehicles.activity.VehiclesListActivity
import com.drivequant.drivekit.vehicle.ui.vehicles.adapter.VehiclesListAdapter
import com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel.VehiclesListViewModel
import kotlinx.android.synthetic.main.fragment_vehicles_list.*
import kotlinx.android.synthetic.main.header_vehicle_list.*

class VehiclesListFragment : Fragment() {
    private lateinit var viewModel : VehiclesListViewModel
    private var adapter: VehiclesListAdapter? = null
    private var shouldSyncVehicles = true
    private lateinit var synchronizationType: SynchronizationType

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_vehicles_list, container, false).setDKStyle()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_vehicles_list"
            ), javaClass.simpleName
        )

        refresh_vehicles.setOnRefreshListener {
            updateVehicles(SynchronizationType.DEFAULT)
        }

        viewModel = ViewModelProviders.of(this).get(VehiclesListViewModel::class.java)
        viewModel.apply {
            progressBarObserver.observe(this@VehiclesListFragment) {
                it?.let { displayProgressCircular ->
                    if (displayProgressCircular) {
                        showProgressCircular()
                    } else {
                        hideProgressCircular()
                    }
                }
            }

            removeBeaconOrBluetoothObserver.observe(this@VehiclesListFragment) {
                viewModel.fetchVehicles(requireContext(), SynchronizationType.CACHE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        synchronizationType = if (viewModel.hasLocalVehicles()) {
            SynchronizationType.CACHE
        } else {
            SynchronizationType.DEFAULT
        }
        updateVehicles(synchronizationType)
    }

    private fun updateTitle(title: String) {
        if (activity is VehiclesListActivity) {
            (activity as VehiclesListActivity).updateTitle(title)
        }
    }

    fun updateVehicles(synchronizationType: SynchronizationType) {
        adapter?.setTouched(false)
        viewModel.vehiclesData.observe(this) {
            if (viewModel.syncStatus == VehicleSyncStatus.FAILED_TO_SYNC_VEHICLES_CACHE_ONLY) {
                Toast.makeText(
                    context,
                    DKResource.convertToString(requireContext(), "dk_vehicle_error_message"),
                    Toast.LENGTH_LONG
                ).show()
            }
            if (it.isNullOrEmpty()) {
                linear_layout_header_vehicle_list.visibility = View.VISIBLE
            } else {
                vehicles_list.layoutManager = LinearLayoutManager(context)
                displayVehiclesList()
                adapter?.let { adapter ->
                    adapter.setVehicles(it)
                    adapter.notifyDataSetChanged()
                } ?: run {
                    adapter = VehiclesListAdapter(requireContext(), viewModel, it.toMutableList())
                    vehicles_list.adapter = adapter
                }
            }
            refresh_vehicles.apply {
                visibility = View.VISIBLE
                isRefreshing = false
            }
            setupVehicleButton()
            updateTitle(viewModel.getScreenTitle(requireContext()))
            if (synchronizationType == SynchronizationType.CACHE && shouldSyncVehicles) {
                shouldSyncVehicles = false
                updateVehicles(SynchronizationType.DEFAULT)
            }
        }
        refresh_vehicles.isRefreshing = true
        viewModel.fetchVehicles(requireContext(), synchronizationType = synchronizationType)
    }

    private fun setupVehicleButton() {
        if (viewModel.shouldDisplayButton()) {
            button_vehicle.apply {
                visibility = View.VISIBLE
                button()
                text = DKResource.convertToString(requireContext(), viewModel.getVehicleButtonTextResId())
                setOnClickListener {
                    if (viewModel.shouldReplaceVehicle()) {
                        VehiclePickerActivity.launchActivity(context, vehicleToDelete = viewModel.vehiclesList.first())
                    } else {
                        VehiclePickerActivity.launchActivity(requireContext())
                    }
                }
            }
        } else {
            button_vehicle.visibility = View.GONE
        }
    }

    private fun displayVehiclesList() {
        if (viewModel.vehiclesList.isNotEmpty()) {
            linear_layout_header_vehicle_list.visibility = View.GONE
            vehicles_list.visibility = View.VISIBLE
        } else {
            setupEmptyListLayout()
            linear_layout_header_vehicle_list.visibility = View.VISIBLE
            vehicles_list.visibility = View.GONE
        }
        hideProgressCircular()
    }

    private fun setupEmptyListLayout() {
        text_view_summary_icon.setImageDrawable(
            DKResource.convertToDrawable(
                requireContext(),
                "dk_common_warning"
            )
        )
        text_view_header_title.apply {
            typeface = Typeface.DEFAULT_BOLD
            text = DKResource.convertToString(requireContext(), "dk_vehicle_list_empty")
        }
    }

    private fun hideProgressCircular() {
        dk_progress_circular?.apply {
            animate()
            .alpha(0f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                }
            })
        }
    }

    private fun showProgressCircular() {
        dk_progress_circular?.apply {
            animate()
                .alpha(1f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.VISIBLE
                    }
                })
        }
    }
}